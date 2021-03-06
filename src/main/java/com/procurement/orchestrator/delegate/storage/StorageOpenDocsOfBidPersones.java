package com.procurement.orchestrator.delegate.storage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.StorageRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.procurement.orchestrator.domain.commands.StorageCommandType.PUBLISH;

@Component
public class StorageOpenDocsOfBidPersones implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(StorageOpenDocsOfBidPersones.class);

    private final StorageRestClient storageRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public StorageOpenDocsOfBidPersones(
        final StorageRestClient storageRestClient,
        final OperationService operationService,
        final ProcessService processService,
        final JsonUtil jsonUtil
    ) {
        this.storageRestClient = storageRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();

        final Optional<JsonNode> documents = getDocumentsOfBusinessFunctions(jsonData, processId);
        if (documents.isPresent()) {
            final JsonNode commandMessage = processService.getCommandMessage(PUBLISH, context, documents.get());
            LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

            final ResponseEntity<ResponseDto> response = storageRestClient.execute(commandMessage);
            LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

            final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
            LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

            if (responseData != null) {
                final JsonNode step = setDocumentsOfBusinessFunctions(jsonData, responseData, processId);
                LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

                operationService.saveOperationStep(execution, entity, commandMessage, step);
            }
        } else {
            LOG.debug("No documents for publishing.");
            operationService.saveOperationStep(execution, entity);
        }
    }

    private Optional<JsonNode> getDocumentsOfBusinessFunctions(final JsonNode jsonData, final String processId) {
        try {
            final ArrayNode bidsNode = (ArrayNode) jsonData.get("bids");

            final ArrayNode documentsNode = jsonUtil.createArrayNode();

            for (final JsonNode bidNode: bidsNode) {
                if (bidNode.has("tenderers")) {
                    final ArrayNode tenderers = (ArrayNode) bidNode.get("tenderers");
                    for (final JsonNode tenderer: tenderers) {
                        if (tenderer.has("persones")) {
                            final ArrayNode persones = (ArrayNode) tenderer.get("persones");
                            for (final JsonNode person : persones) {
                                if (person.has("businessFunctions")) {
                                    final ArrayNode businessFunctionsArray = (ArrayNode) person.get("businessFunctions");
                                    for (final JsonNode businessFunction : businessFunctionsArray) {
                                        if (businessFunction.has("documents")) {
                                            final ArrayNode documentsArray = (ArrayNode) businessFunction.get("documents");
                                            for (final JsonNode document : documentsArray) {
                                                documentsNode.add(document);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (documentsNode.size() == 0)
                return Optional.empty();

            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.set("documents", documentsNode);
            return Optional.of(mainNode);
        } catch (Exception e) {
            LOG.error("Error getting documents of businessFunctions.", e);
            processService.terminateProcess(processId, e.getMessage());
            return Optional.empty();
        }
    }

    private JsonNode setDocumentsOfBusinessFunctions(final JsonNode jsonData, final JsonNode updatedDocumentsData, final String processId) {
        try {
            final Map<String, JsonNode> updatedDocumentsByIds = updatedDocumentsByIds(updatedDocumentsData);
            if (updatedDocumentsByIds.isEmpty())
                return jsonData;

            final ArrayNode bidsNode = (ArrayNode) jsonData.get("bids");

            for (final JsonNode bidNode: bidsNode) {
                if (bidNode.has("tenderers")) {
                    final ArrayNode tenderers = (ArrayNode) bidNode.get("tenderers");
                    for (final JsonNode tenderer: tenderers) {
                        if (tenderer.has("persones")) {
                            final ArrayNode persones = (ArrayNode) tenderer.get("persones");
                            for (final JsonNode person : persones) {
                                if (person.has("businessFunctions")) {
                                    final ArrayNode businessFunctionsArray = (ArrayNode) person.get("businessFunctions");
                                    for (final JsonNode businessFunction : businessFunctionsArray) {
                                        if (businessFunction.has("documents")) {
                                            final ArrayNode documentsNode = jsonUtil.createArrayNode();
                                            final ArrayNode oldDocumentsArray = (ArrayNode) businessFunction.get("documents");
                                            for (final JsonNode oldDocument : oldDocumentsArray) {
                                                final String oldId = oldDocument.get("id").asText();
                                                final JsonNode updatedDocument = updatedDocumentsByIds.get(oldId);
                                                documentsNode.add(updatedDocument);
                                            }
                                            ((ObjectNode) businessFunction).set("documents", documentsNode);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return jsonData;
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    private Map<String, JsonNode> updatedDocumentsByIds(final JsonNode updatedDocumentsData) {
        if (!updatedDocumentsData.has("documents"))
            return Collections.emptyMap();

        final Map<String, JsonNode> updatedDocumentsByIds = new HashMap<>();
        final ArrayNode responseDocuments = (ArrayNode) updatedDocumentsData.get("documents");
        for (final JsonNode updatedDocument : responseDocuments) {
            final String updatedDocumentId = updatedDocument.get("id").asText();
            updatedDocumentsByIds.put(updatedDocumentId, updatedDocument);
        }
        return updatedDocumentsByIds;
    }
}

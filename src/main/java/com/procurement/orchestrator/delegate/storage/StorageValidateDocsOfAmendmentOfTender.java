package com.procurement.orchestrator.delegate.storage;

import com.fasterxml.jackson.databind.JsonNode;
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

import java.util.Optional;

import static com.procurement.orchestrator.domain.commands.StorageCommandType.VALIDATE;

@Component
public class StorageValidateDocsOfAmendmentOfTender implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(StorageValidateDocsOfAmendmentOfTender.class);

    private final StorageRestClient storageRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public StorageValidateDocsOfAmendmentOfTender(
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

        final Optional<JsonNode> result = processService.getDocumentsOfAmendmentsOfTender(jsonData, processId);
        if (result.isPresent()) {
            final JsonNode documents = result.get();
            if (!documents.isNull()) {
                final JsonNode commandMessage = processService.getCommandMessage(VALIDATE, context, documents);
                if (LOG.isDebugEnabled())
                    LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

                final ResponseEntity<ResponseDto> response = storageRestClient.execute(commandMessage);
                if (LOG.isDebugEnabled())
                    LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

                final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
                if (LOG.isDebugEnabled())
                    LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

                if (responseData != null) {
                    operationService.saveOperationStep(execution, entity, commandMessage);
                }
            } else {
                if (LOG.isDebugEnabled())
                    LOG.debug("No documents for validation.");
                operationService.saveOperationStep(execution, entity, context);
            }
        }
    }
}

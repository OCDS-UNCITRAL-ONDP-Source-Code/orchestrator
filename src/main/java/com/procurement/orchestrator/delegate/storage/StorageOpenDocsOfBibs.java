package com.procurement.orchestrator.delegate.storage;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.StorageRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.procurement.orchestrator.domain.commands.StorageCommandType.PUBLISH;

@Component
public class StorageOpenDocsOfBibs implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(StorageOpenDocsOfBibs.class);

    private final StorageRestClient storageRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public StorageOpenDocsOfBibs(final StorageRestClient storageRestClient,
                                 final OperationService operationService,
                                 final ProcessService processService,
                                 final JsonUtil jsonUtil) {
        this.storageRestClient = storageRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityName();
        final JsonNode documents = processService.getDocumentsOfBids(jsonData, processId);
        final JsonNode commandMessage = processService.getCommandMessage(PUBLISH, context, documents);
        JsonNode responseData = null;
        if (Objects.nonNull(documents)) {
            responseData = processService.processResponse(
                    storageRestClient.execute(commandMessage),
                    context,
                    processId,
                    taskId,
                    commandMessage);
        }
        if (Objects.nonNull(responseData)) {
            operationService.saveOperationStep(
                    execution,
                    entity,
                    commandMessage,
                    processService.setDocumentsOfBids(jsonData, responseData, processId));
        }
    }
}

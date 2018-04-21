package com.procurement.orchestrator.delegate.storage;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.StorageRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StorageOpenDocsOfTender implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(StorageOpenDocsOfTender.class);

    private final StorageRestClient storageRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public StorageOpenDocsOfTender(final StorageRestClient storageRestClient,
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
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        final String processId = execution.getProcessInstanceId();
        final String startDate = params.getStartDate();
        final JsonNode documents = processService.getDocumentsOfTender(jsonData, processId);
        final String taskId = execution.getCurrentActivityName();
        JsonNode responseData = null;
        if (Objects.nonNull(documents))
            responseData = processService.processResponse(
                    storageRestClient.setPublishDate(startDate, documents),
                    params,
                    processId,
                    taskId,
                    documents);
        if (Objects.nonNull(responseData))
            operationService.saveOperationStep(
                    execution,
                    entity,
                    documents,
                    processService.setDocumentsOfTender(jsonData, responseData, processId));
    }
}

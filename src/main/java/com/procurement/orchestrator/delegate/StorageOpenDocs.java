package com.procurement.orchestrator.delegate;

import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.rest.StorageRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StorageOpenDocs implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(StorageOpenDocs.class);

    private final StorageRestClient storageRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public StorageOpenDocs(final StorageRestClient storageRestClient,
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
//        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
//        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
//        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
//        final String processId = execution.getProcessInstanceId();
//        final String operationId = params.getOperationId();
//        final String startDate = params.getStartDate();
//        final JsonNode documents = processService.getDocuments(jsonData, processId, operationId);
//        final String taskId = execution.getCurrentActivityName();
//        JsonNode responseData = processService.processResponse(
//                storageRestClient.setPublishDateBatch(startDate, documents),
//                processId,
//                operationId,
//                taskId);
//        if (Objects.nonNull(responseData))
//            operationService.saveOperationStep(
//                    execution,
//                    entity,
//                    processService.setDatePublished(jsonData, startDate, processId, operationId));
    }
}

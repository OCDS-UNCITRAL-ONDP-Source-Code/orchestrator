package com.procurement.orchestrator.delegate.tender.qualification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.rest.QualificationRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QualificationCheckAwarded implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(QualificationCheckAwarded.class);

    private final QualificationRestClient qualificationRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public QualificationCheckAwarded(QualificationRestClient qualificationRestClient,
                                     final OperationService operationService,
                                     final ProcessService processService,
                                     final JsonUtil jsonUtil) {
        this.qualificationRestClient = qualificationRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        final String processId = execution.getProcessInstanceId();
        final String operationId = params.getOperationId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode responseData = processService.processResponse(
                qualificationRestClient.checkAwarded(params.getCpid(), params.getStage(), params.getEndDate()),
                processId,
                operationId,
                taskId);
        if (Objects.nonNull(responseData)) {
            final Boolean allAwarded = getAllAwarded(responseData, processId);
            execution.setVariable("checkEnquiries", (allAwarded ? 1 : 0));
            operationService.saveOperationStep(execution, entity, responseData);
        }
    }

    private Boolean getAllAwarded(final JsonNode responseData, final String processId) {
        return processService.getBoolean("allAwarded", responseData, processId);
    }

}

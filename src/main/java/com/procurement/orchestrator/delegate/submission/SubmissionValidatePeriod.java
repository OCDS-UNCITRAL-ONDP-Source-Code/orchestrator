package com.procurement.orchestrator.delegate.submission;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.SubmissionRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SubmissionValidatePeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SubmissionValidatePeriod.class);

    private final SubmissionRestClient submissionRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;


    public SubmissionValidatePeriod(final SubmissionRestClient submissionRestClient,
                                    final OperationService operationService,
                                    final ProcessService processService,
                                    final JsonUtil jsonUtil) {
        this.submissionRestClient = submissionRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        processService.addTenderPeriodStartDate(jsonData, params.getStartDate(), processId);
        params.setEndDate(processService.getTenderPeriodEndDate(jsonData, processId));
        final JsonNode responseData = processService.processResponse(
                submissionRestClient.periodValidation(
                        params.getCountry(),
                        params.getPmd(),
                        params.getStartDate(),
                        params.getEndDate()),
                params,
                processId,
                taskId);
        if (Objects.nonNull(responseData)) {
            operationService.saveOperationStep(execution, entity, params, jsonData);
        }
    }
}

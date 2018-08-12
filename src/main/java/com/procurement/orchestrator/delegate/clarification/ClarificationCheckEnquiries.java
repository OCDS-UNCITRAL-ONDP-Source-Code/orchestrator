package com.procurement.orchestrator.delegate.clarification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.ClarificationRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ClarificationCheckEnquiries implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ClarificationCheckEnquiries.class);

    private final ClarificationRestClient clarificationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public ClarificationCheckEnquiries(final ClarificationRestClient clarificationRestClient,
                                       final OperationService operationService,
                                       final ProcessService processService,
                                       final JsonUtil jsonUtil) {
        this.clarificationRestClient = clarificationRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final JsonNode requestData = jsonUtil.toJsonNode(entity.getResponseData());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode responseData = processService.processResponse(
                clarificationRestClient.checkEnquiries(context.getCpid(), context.getStage()),
                context,
                processId,
                taskId,
                requestData);
        if (Objects.nonNull(responseData)) {
            processContext(execution, context, responseData, processId);
            operationService.saveOperationStep(execution, entity, context, requestData, responseData);
        }
    }

    private void processContext(final DelegateExecution execution, final Context context, final JsonNode responseData, final String processId) {
        final Boolean allAnswered = processService.getBoolean("allAnswered", responseData, processId);
        if (allAnswered != null) {
            if (allAnswered) {
                execution.setVariable("checkEnquiries", 1);
            } else {
                execution.setVariable("checkEnquiries", 2);
                context.setOperationType("suspendTender");
            }
        } else {
            final String endDate = processService.getText("tenderPeriodEndDate", responseData, processId);
            execution.setVariable("checkEnquiries", 3);
            context.setEndDate(endDate);
        }
    }
}

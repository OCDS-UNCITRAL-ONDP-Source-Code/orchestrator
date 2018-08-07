package com.procurement.orchestrator.delegate.clarification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.ClarificationRestClient;
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
public class ClarificationCreateAnswer implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ClarificationCreateAnswer.class);

    private final ClarificationRestClient clarificationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public ClarificationCreateAnswer(final ClarificationRestClient clarificationRestClient,
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
                clarificationRestClient.updateEnquiry(
                        context.getCpid(),
                        context.getStage(),
                        context.getToken(),
                        context.getOwner(),
                        context.getStartDate(),
                        requestData),
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
        execution.setVariable("allAnswered", allAnswered ? 1 : 0);
        if (!allAnswered) {
            context.setOperationType("addAnswer");
        } else {
            context.setOperationType("unsuspendTender");
        }
    }
}

package com.procurement.orchestrator.delegate.qualification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.QualificationRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.procurement.orchestrator.domain.commands.QualificationCommandType.SET_FINAL_STATUSES;

@Component
public class QualificationSetFinalStatuses implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(QualificationSetFinalStatuses.class);

    private final QualificationRestClient qualificationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public QualificationSetFinalStatuses(final QualificationRestClient qualificationRestClient,
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
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        context.setOperationType("awardPeriodEnd");
        final JsonNode commandMessage = processService.getCommandMessage(SET_FINAL_STATUSES, context, jsonUtil.empty());
        final JsonNode responseData = processService.processResponse(
                qualificationRestClient.execute(commandMessage),
                context,
                processId,
                taskId,
                commandMessage);
        if (Objects.nonNull(responseData)) {
            operationService.saveOperationStep(execution, entity, context, commandMessage, responseData);
        }
    }
}

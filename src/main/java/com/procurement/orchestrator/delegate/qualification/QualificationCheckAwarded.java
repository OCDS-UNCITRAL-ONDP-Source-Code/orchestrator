package com.procurement.orchestrator.delegate.qualification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.QualificationRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.procurement.orchestrator.domain.commands.QualificationCommandType.CHECK_AWARDED;

@Component
public class QualificationCheckAwarded implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(QualificationCheckAwarded.class);

    private final QualificationRestClient qualificationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;
    private final DateUtil dateUtil;

    public QualificationCheckAwarded(final QualificationRestClient qualificationRestClient,
                                     final OperationService operationService,
                                     final ProcessService processService,
                                     final JsonUtil jsonUtil,
                                     final DateUtil dateUtil) {
        this.qualificationRestClient = qualificationRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode commandMessage = processService.getCommandMessage(CHECK_AWARDED, context, jsonUtil.empty());
        final JsonNode responseData = processService.processResponse(
                qualificationRestClient.execute(commandMessage),
                context,
                processId,
                taskId,
                commandMessage);
        if (Objects.nonNull(responseData)) {
            operationService.saveOperationStep(execution, entity, commandMessage, responseData);
        }
    }
}

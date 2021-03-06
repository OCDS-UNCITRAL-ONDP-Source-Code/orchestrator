package com.procurement.orchestrator.delegate.contracting;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.OperationType;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.ContractingRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.procurement.orchestrator.domain.commands.ContractingCommandType.CANCEL_CAN;

@Component
public class ContractingCancelCan implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ContractingCancelCan.class);

    private final ContractingRestClient contractingRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public ContractingCancelCan(final ContractingRestClient contractingRestClient,
                                final OperationService operationService,
                                final ProcessService processService,
                                final JsonUtil jsonUtil) {
        this.contractingRestClient = contractingRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();

        final JsonNode commandMessage = processService.getCommandMessage(CANCEL_CAN, context, jsonData);
        if (LOG.isDebugEnabled())
            LOG.debug("COMMAND (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(commandMessage) + "'.");

        final ResponseEntity<ResponseDto> response = contractingRestClient.execute(commandMessage);
        if (LOG.isDebugEnabled()) {
            final ResponseDto body = response.getBody();
            final String result = jsonUtil.toJson(body);
            LOG.debug("RESPONSE FROM SERVICE (" + context.getOperationId() + "): '" + result + "'.");
        }

        final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE AFTER PROCESSING (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(responseData) + "'.");

        if (Objects.nonNull(responseData)) {
            if (responseData.get("contract") != null) {
                context.setOperationType(OperationType.CANCEL_CAN_CONTRACT.value());
            }
            final String lotId = processService.getText("lotId", responseData, processId);
            context.setId(lotId);
            if (LOG.isDebugEnabled())
                LOG.debug("CONTEXT FOR SAVE (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(context) + "'.");

            final JsonNode step = processService.addCancelCan(jsonData, responseData, processId);
            if (LOG.isDebugEnabled())
                LOG.debug("STEP FOR SAVE (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(step) + "'.");

            operationService.saveOperationStep(execution, entity, context, commandMessage, step);
        }
    }
}

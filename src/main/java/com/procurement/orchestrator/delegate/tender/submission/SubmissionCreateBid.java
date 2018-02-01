package com.procurement.orchestrator.delegate.tender.submission;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.delegate.tender.access.AccessCreateCn;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.rest.SubmissionRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SubmissionCreateBid implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SubmissionCreateBid.class);

    private final SubmissionRestClient submissionRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public SubmissionCreateBid(final SubmissionRestClient submissionRestClient,
                               final OperationService operationService,
                               final ProcessService processService,
                               final JsonUtil jsonUtil) {
        this.submissionRestClient = submissionRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final Optional<OperationStepEntity> entityOptional = operationService.getPreviousOperationStep(execution);
        if (entityOptional.isPresent()) {
            final OperationStepEntity entity = entityOptional.get();
            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
            final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
            final String processId = execution.getProcessInstanceId();
            final String operationId = params.getOperationId();
            try {
                final JsonNode responseData = processService.processResponse(
                        submissionRestClient.createBid(params.getCpid(), "ps", params.getOwner(), jsonData),
                        processId,
                        operationId);
                if (Objects.nonNull(responseData))
                operationService.saveOperationStep(
                        execution,
                        entity,
                        addTokenToParams(params, responseData, processId, operationId),
                        responseData);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                processService.processException(e.getMessage(), execution.getProcessInstanceId());
            }
        }
    }

    private Params addTokenToParams(final Params params,
                                    final JsonNode responseData,
                                    final String processId,
                                    final String operationId) {
        try {
            if (responseData.get("token") != null) {
                params.setToken(responseData.get("token").asText());
            }
            return params;
        } catch (Exception e) {
            processService.processError(e.getMessage(), processId, operationId);
            return null;
        }
    }
}
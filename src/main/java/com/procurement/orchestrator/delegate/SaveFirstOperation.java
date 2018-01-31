package com.procurement.orchestrator.delegate;

import com.procurement.orchestrator.cassandra.model.RequestEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.cassandra.service.RequestService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SaveFirstOperation implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SaveFirstOperation.class);

    private final OperationService operationService;

    private final ProcessService processService;

    private final RequestService requestService;

    private final DateUtil dateUtil;

    public SaveFirstOperation(final RequestService requestService,
                              final OperationService operationService,
                              final ProcessService processService,
                              final DateUtil dateUtil) {
        this.requestService = requestService;
        this.operationService = operationService;
        this.processService = processService;
        this.dateUtil = dateUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final Optional<RequestEntity> requestOptional = requestService.getRequestById(
                (String) execution.getVariable("requestId"));
        if (requestOptional.isPresent()) {
            if (!operationService.saveIfNotExist(execution.getProcessBusinessKey(), execution.getProcessInstanceId())) {
                processService.terminateProcess(execution.getProcessInstanceId());
            }
            operationService.saveFirstOperationStep(execution, requestOptional.get());
        } else {
            processService.terminateProcess(execution.getProcessInstanceId());
        }
    }
}

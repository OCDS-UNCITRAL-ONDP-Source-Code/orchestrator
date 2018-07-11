package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Rules;
import com.procurement.orchestrator.domain.Stage;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.domain.entity.RequestEntity;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service
public interface OperationService {

    Boolean isRulesExist(Rules rules);

    void checkOperationById(String operationId);

    Boolean saveIfNotExist(String operationId, String processId);

    void saveFirstOperationStep(DelegateExecution execution, RequestEntity entity);

    OperationStepEntity getOperationStep(String processId, String taskId);

    OperationStepEntity getPreviousOperationStep(DelegateExecution execution);

    void saveOperationStep(DelegateExecution execution, OperationStepEntity entity);

    void saveOperationStep(DelegateExecution execution, OperationStepEntity entity, Context context);

    void saveOperationStep(DelegateExecution execution, OperationStepEntity entity, JsonNode request);

    void saveOperationStep(DelegateExecution execution, OperationStepEntity entity, Context context, JsonNode request);

    void saveOperationStep(DelegateExecution execution, OperationStepEntity entity, JsonNode request, JsonNode response);

    void saveOperationStep(DelegateExecution execution, OperationStepEntity entity, Context context, JsonNode request, JsonNode response);

    void saveOperationException(String processId, String taskId, Context context, JsonNode request, JsonNode response);

    void saveContext(Context context);

    Context getContext(String cpId, String processId);

    Stage getStageFromRules(String country, String pmd, String operationType);
}



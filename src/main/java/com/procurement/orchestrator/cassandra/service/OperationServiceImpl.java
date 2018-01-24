package com.procurement.orchestrator.cassandra.service;

import com.procurement.orchestrator.cassandra.dao.CassandraDao;
import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.exception.OperationException;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service
public class OperationServiceImpl implements OperationService {

    private final CassandraDao cassandraDao;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public OperationServiceImpl(final CassandraDao cassandraDao,
                                final JsonUtil jsonUtil,
                                final DateUtil dateUtil) {
        this.cassandraDao = cassandraDao;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public void checkOperationById(final String operationId) {
        if (cassandraDao.isOperationExist(operationId)) {
            throw new OperationException("Operation with current id already exist.");
        }
    }

    @Override
    public Boolean saveIfNotExist(final String operationId,
                                  final String processId) {
        OperationEntity operationEntity = new OperationEntity();
        operationEntity.setOperationId(operationId);
        operationEntity.setProcessId(processId);
        return cassandraDao.saveOperationIfNotExist(operationEntity);
    }

    @Override
    public void saveFirstOperationStep(final OperationStepEntity entity) {
        cassandraDao.saveOperationStep(entity);
    }

    @Override
    public Optional<OperationStepEntity> getOperationStep(final String processId, final String taskId) {
        return cassandraDao.getOperationStep(processId, taskId);
    }

    @Override
    public void saveOperationStep(final String taskId,
                                  final OperationStepEntity entity,
                                  final Params params,
                                  final Object response
    ) {
        entity.setTaskId(taskId);
        entity.setJsonParams(jsonUtil.toJson(params));
        entity.setJsonData(jsonUtil.toJson(jsonUtil.toJsonNode(response)));
        entity.setDate(dateUtil.getNowUTC());
        cassandraDao.saveOperationStep(entity);
    }

    @Override
    public void saveOperationStep(final String taskId,
                                  final OperationStepEntity entity,
                                  final Object response) {
        entity.setTaskId(taskId);
        entity.setJsonData(jsonUtil.toJson(jsonUtil.toJsonNode(response)));
        entity.setDate(dateUtil.getNowUTC());
        cassandraDao.saveOperationStep(entity);
    }

    @Override
    public void saveOperationStep(final String taskId,
                                  final OperationStepEntity entity) {
        entity.setTaskId(taskId);
        entity.setDate(dateUtil.getNowUTC());
        cassandraDao.saveOperationStep(entity);
    }
}


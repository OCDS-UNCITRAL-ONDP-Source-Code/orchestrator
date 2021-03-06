package com.procurement.orchestrator.infrastructure.bpms.delegate.mdm

import com.procurement.orchestrator.application.client.MdmClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asFailure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.tender.criteria.Criteria
import com.procurement.orchestrator.domain.model.tender.criteria.CriterionId
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroup
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroups
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractBatchRestDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRequirementGroups
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRequirementGroupsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class MdmGetRequirementGroupsDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform,
    private val mdmClient: MdmClient
) : AbstractBatchRestDelegate<Unit, GetRequirementGroupsAction.Params, Map<CriterionId, List<RequirementGroup>>>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override fun prepareSeq(
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<List<GetRequirementGroupsAction.Params>, Fail.Incident> {

        val requestInfo = context.requestInfo
        val processInfo = context.processInfo

        val tender = context.tryGetTender()
            .orForwardFail { error -> return error }

        return tender.criteria
            .map { criterion ->
                GetRequirementGroupsAction.Params(
                    lang = requestInfo.language,
                    scheme = requestInfo.country,
                    pmd = processInfo.pmd,
                    country = requestInfo.country,
                    phase = processInfo.phase,
                    criterionId = criterion.id
                )
            }
            .asSuccess()
    }

    override suspend fun execute(
        elements: List<GetRequirementGroupsAction.Params>,
        executionInterceptor: ExecutionInterceptor
    ): Result<Map<CriterionId, List<RequirementGroup>>, Fail.Incident> {

        return elements
            .map { params ->
                val requirementGroups = mdmClient.getRequirementGroups(params = params)
                    .orForwardFail { error -> return error }
                    .let {
                        handleResult(it)
                    }

                if (requirementGroups.isEmpty())
                    return Fail.Incident.Response.Empty(service = ExternalServiceName.MDM, action = "GetRequirementGroups ($params)")
                        .asFailure()

                params.criterionId to requirementGroups
            }
            .toMap()
            .asSuccess()
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Map<CriterionId, List<RequirementGroup>>
    ): MaybeFail<Fail.Incident> {

        if (result.isEmpty())
            return MaybeFail.none()

        val tender = context.tryGetTender()
            .orReturnFail { return MaybeFail.fail(it) }

        val updatedCriteria = tender.criteria
            .map { criterion ->
                val requirementGroups = result.getValue(criterion.id)
                criterion.copy(requirementGroups = RequirementGroups(requirementGroups))
            }

        context.tender = tender.copy(criteria = Criteria(updatedCriteria))

        return MaybeFail.none()
    }

    private fun handleResult(response: GetRequirementGroups.Result): List<RequirementGroup> = when (response) {
        is GetRequirementGroups.Result.Success -> {
            response.requirementGroups
                .map { requirementGroup ->
                    RequirementGroup(
                        id = requirementGroup.id,
                        description = requirementGroup.description
                    )
                }
        }
    }
}


package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.party.PartyRole
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.model.tender.criteria.Criteria
import com.procurement.orchestrator.domain.model.tender.criteria.CriteriaSource
import com.procurement.orchestrator.domain.util.extension.getNewElements
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.AccessCommands
import com.procurement.orchestrator.infrastructure.client.web.access.action.FindCriteriaAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.convertToContextEntity
import org.springframework.stereotype.Component

@Component
class AccessFindCriteriaDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<AccessFindCriteriaDelegate.Parameters, FindCriteriaAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val PARAMETER_NAME_SOURCE = "source"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val source: CriteriaSource = parameterContainer.getString(PARAMETER_NAME_SOURCE)
            .orForwardFail { fail -> return fail }
            .let { role ->
                CriteriaSource.orNull(role)
                    ?: return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_NAME_SOURCE,
                            actualValue = role,
                            expectedValues = PartyRole.allowedElements.keysAsStrings()
                        )
                    )
            }
        return success(Parameters(source = source))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<FindCriteriaAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        return accessClient.findCriteria(
            id = commandId,
            params = FindCriteriaAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                source = parameters.source
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: Option<FindCriteriaAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = "eAccess",
                    action = AccessCommands.FindCriteria
                )
            )

        val tender = context.tender ?: Tender()

        val receivedCriteria = data
            .map { it.convertToContextEntity() }

        val receivedCriteriaIds = receivedCriteria
            .map { it.id }

        val availableCriteriaDbIds = tender.criteria
            .map { it.id }

        val receivedCriteriaById = receivedCriteria
            .associateBy { it.id }

        val updatedCriteria = tender.criteria
            .map { criteria ->
                receivedCriteriaById[criteria.id]
                    ?.let { rqCriteria -> criteria.updateBy(rqCriteria) }
                    ?: criteria
            }

        val newCriteriaIds = getNewElements(received = receivedCriteriaIds, known = availableCriteriaDbIds)
        val newCriteria = newCriteriaIds
            .map { criteriaId -> receivedCriteriaById.getValue(criteriaId) }

        val updatedTender = tender.copy(
            criteria = Criteria(updatedCriteria + newCriteria)
        )

        context.tender = updatedTender

        return MaybeFail.none()
    }

    data class Parameters(val source: CriteriaSource)
}

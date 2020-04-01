package com.procurement.orchestrator.infrastructure.bpms.delegate.dossier

import com.procurement.orchestrator.application.client.DossierClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAwardIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getRequirementResponseIfOnlyOne
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.ValidateRequirementResponseAction
import org.springframework.stereotype.Component

@Component
class DossierValidateRequirementResponseDelegate(
    logger: Logger,
    private val dossierClient: DossierClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<Unit>, Fail.Incident> {

        val processInfo = context.processInfo
        val cpid: Cpid = processInfo.cpid
        val ocid: Ocid = processInfo.ocid

        val award = context.awards.getAwardIfOnlyOne()
            .doOnError { return failure(it) }
            .get

        val requirementResponse = award.requirementResponses
            .getRequirementResponseIfOnlyOne()
            .doOnError { return failure(it) }
            .get

        return dossierClient.validateRequirementResponse(
            params = ValidateRequirementResponseAction.Params(
                cpid = cpid,
                ocid = ocid,
                requirementResponse = ValidateRequirementResponseAction.Params.RequirementResponse(
                    id = requirementResponse.id as RequirementResponseId.Temporal,
                    value = requirementResponse.value,
                    requirement = requirementResponse.requirement
                        ?.let { requirement ->
                            ValidateRequirementResponseAction.Params.RequirementResponse.Requirement(
                                id = requirement.id
                            )
                        }
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: Unit
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()
}

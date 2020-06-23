package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe

import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.GlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAmendmentsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getAwardsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getDetailsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getRequirementResponseIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.tryGetSubmissions
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asOption
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.amendment.Amendment
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.domain.model.amendment.Amendments
import com.procurement.orchestrator.domain.model.award.Awards
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.domain.model.submission.Details
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractInternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component
import java.io.Serializable

@Component
class BpeCreateIdsDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractInternalDelegate<BpeCreateIdsDelegate.Parameters, BpeCreateIdsDelegate.Ids>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val PARAMETER_NAME_LOCATION = "location"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val location = parameterContainer.getString(PARAMETER_NAME_LOCATION)
            .orForwardFail { fail -> return fail }
            .let {
                Location.orNull(it)
                    ?: return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_NAME_LOCATION,
                            expectedValues = Location.allowedElements.keysAsStrings(),
                            actualValue = it
                        )
                    )
            }
        return Parameters(location = location)
            .asSuccess()
    }

    override suspend fun execute(
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Option<Ids>, Fail.Incident> = when (parameters.location) {
        Location.AWARD_REQUIREMENT_RESPONSE -> generatePermanentAwardRequirementResponsesIds(context)
            .orForwardFail { fail -> return fail }

        Location.SUBMISSION_DETAILS -> generatePermanentSubmissionsIds(context)
            .orForwardFail { fail -> return fail }

        Location.TENDER_AMENDMENT -> generatePermanentTenderAmendmentsIds(context)
            .orForwardFail { fail -> return fail }
    }
        .asOption()
        .asSuccess()

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        data: Ids
    ): MaybeFail<Fail.Incident> = when (data) {
        is Ids.AwardRequirementResponses -> updateAwardRequirementResponsesIds(context, data)
        is Ids.Submissions -> updateSubmissionsIds(context, data)
        is Ids.TenderAmendments -> updateTenderAmendmentsIds(context, data)
    }

    private fun generatePermanentAwardRequirementResponsesIds(context: GlobalContext): Result<Ids, Fail.Incident> =
        context.getAwardsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .flatMap { award ->
                award.getRequirementResponseIfNotEmpty()
                    .orForwardFail { fail -> return fail }
            }
            .asSequence()
            .map { requirementResponse ->
                val temporal = requirementResponse.id
                val permanent = RequirementResponseId.generate()
                temporal to permanent
            }
            .toMap()
            .let {
                success(Ids.AwardRequirementResponses(it))
            }

    private fun generatePermanentSubmissionsIds(context: GlobalContext): Result<Ids, Fail.Incident> =
        context.tryGetSubmissions()
            .orForwardFail { fail -> return fail }
            .getDetailsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .asSequence()
            .map { submission ->
                val temporal = submission.id
                val permanent = SubmissionId.generate()
                temporal to permanent
            }
            .toMap()
            .let {
                success(Ids.Submissions(it))
            }

    private fun generatePermanentTenderAmendmentsIds(context: GlobalContext): Result<Ids, Fail.Incident> =
        context.tryGetTender()
            .orForwardFail { fail -> return fail }
            .getAmendmentsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .asSequence()
            .map { amendment ->
                val temporal = amendment.id
                val permanent = AmendmentId.generate()
                temporal to permanent
            }
            .toMap()
            .let {
                success(Ids.TenderAmendments(it))
            }

    private fun updateAwardRequirementResponsesIds(
        context: CamundaGlobalContext,
        ids: Ids.AwardRequirementResponses
    ): MaybeFail<Fail.Incident.Bpms.Context> {
        val updatedAwards = context.getAwardsIfNotEmpty()
            .orReturnFail { return MaybeFail.fail(it) }
            .map { award ->
                val updatedRequirementResponses = award.getRequirementResponseIfNotEmpty()
                    .orReturnFail { return MaybeFail.fail(it) }
                    .map { requirementResponse ->
                        ids.getValue(requirementResponse.id)
                            .let { requirementResponse.copy(id = it) }
                    }
                    .let {
                        RequirementResponses(it)
                    }
                award.copy(requirementResponses = updatedRequirementResponses)
            }
            .let { Awards(it) }

        context.awards = updatedAwards

        return MaybeFail.none()
    }

    private fun updateSubmissionsIds(
        context: CamundaGlobalContext,
        ids: Ids.Submissions
    ): MaybeFail<Fail.Incident.Bpms.Context> {
        val submissions = context.tryGetSubmissions()
            .orReturnFail { return MaybeFail.fail(it) }

        val updatedDetails = submissions.getDetailsIfNotEmpty()
            .orReturnFail { return MaybeFail.fail(it) }
            .map { submission ->
                ids.getValue(submission.id)
                    .let { submission.copy(id = it) }

            }
            .let { Details(it) }

        val updatedSubmissions = submissions.copy(
            details = updatedDetails
        )

        context.submissions = updatedSubmissions

        return MaybeFail.none()
    }

    private fun updateTenderAmendmentsIds(
        context: CamundaGlobalContext,
        ids: Ids.TenderAmendments
    ): MaybeFail<Fail.Incident.Bpms.Context> {
        val tender = context.tryGetTender()
            .orReturnFail { return MaybeFail.fail(it) }
        val amendments: List<Amendment> = tender.getAmendmentsIfNotEmpty()
            .orReturnFail { return MaybeFail.fail(it) }

        val updatedAmendments = Amendments(
            values = amendments.map { amendment ->
                ids.getValue(amendment.id)
                    .let { amendment.copy(id = it) }
            }
        )

        val updatedTender = tender.copy(
            amendments = updatedAmendments
        )

        context.tender = updatedTender

        return MaybeFail.none()
    }

    sealed class Ids : Serializable {

        class AwardRequirementResponses(values: Map<RequirementResponseId, RequirementResponseId> = emptyMap()) :
            Ids(), Map<RequirementResponseId, RequirementResponseId> by values, Serializable

        class Submissions(values: Map<SubmissionId, SubmissionId> = emptyMap()) :
            Ids(), Map<SubmissionId, SubmissionId> by values, Serializable

        class TenderAmendments(values: Map<AmendmentId, AmendmentId> = emptyMap()) :
            Ids(), Map<AmendmentId, AmendmentId> by values, Serializable
    }

    class Parameters(val location: Location)

    enum class Location(override val key: String) : EnumElementProvider.Key {

        AWARD_REQUIREMENT_RESPONSE("award.requirementResponse"),
        SUBMISSION_DETAILS("submission"),
        TENDER_AMENDMENT("tender.amendment");

        override fun toString(): String = key

        companion object : EnumElementProvider<Location>(info = info())
    }
}

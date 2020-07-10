package com.procurement.orchestrator.infrastructure.client.web.qualification.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.domain.model.measure.Scoring
import com.procurement.orchestrator.domain.model.organization.OrganizationId
import com.procurement.orchestrator.domain.model.person.PersonId
import com.procurement.orchestrator.domain.model.qualification.QualificationId
import com.procurement.orchestrator.domain.model.qualification.QualificationStatus
import com.procurement.orchestrator.domain.model.qualification.QualificationStatusDetails
import com.procurement.orchestrator.domain.model.requirement.RequirementId
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import com.procurement.orchestrator.domain.model.tender.criteria.CriteriaRelatesTo
import com.procurement.orchestrator.domain.model.tender.criteria.CriteriaSource
import com.procurement.orchestrator.domain.model.tender.criteria.QualificationSystemMethod
import com.procurement.orchestrator.domain.model.tender.criteria.ReductionCriteria
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementDataType
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroupId
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueDeserializer
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueSerializer
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class SetNextForQualificationAction : FunctionalAction<SetNextForQualificationAction.Params, SetNextForQualificationAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "setNextForQualification"
    override val target: Target<Result> = Target.plural()

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("submissions") @field:JsonProperty("submissions") val submissions: List<Submission>,
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("criteria") @field:JsonProperty("criteria") val criteria: List<Criteria>?
    ) {
        data class Submission(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: SubmissionId,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime?
        )

        data class Tender(
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("otherCriteria") @field:JsonProperty("otherCriteria") val otherCriteria: OtherCriteria?
        ) {
            data class OtherCriteria(
                @param:JsonProperty("qualificationSystemMethods") @field:JsonProperty("qualificationSystemMethods") val qualificationSystemMethods: List<QualificationSystemMethod>,
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("reductionCriteria") @field:JsonProperty("reductionCriteria") val reductionCriteria: ReductionCriteria?
            )
        }

        data class Criteria(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("title") @field:JsonProperty("title") val title: String?,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("source") @field:JsonProperty("source") val source: CriteriaSource?,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("relatesTo") @field:JsonProperty("relatesTo") val relatesTo: CriteriaRelatesTo?,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("relatedItem") @field:JsonProperty("relatedItem") val relatedItem: String?,
            @param:JsonProperty("requirementGroups") @field:JsonProperty("requirementGroups") val requirementGroups: List<RequirementGroup>
        ) {
            data class RequirementGroup(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementGroupId,
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("descriptions") @field:JsonProperty("descriptions") val descriptions: String?,
                @param:JsonProperty("requirements") @field:JsonProperty("requirements") val requirements: List<Requirement>
            ) {
                data class Requirement(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementId,
                    @param:JsonProperty("title") @field:JsonProperty("title") val title: String,
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("dataType") @field:JsonProperty("dataType") val dataType: RequirementDataType?
                )
            }
        }
    }

    class Result(@param:JsonProperty("qualifications") @field:JsonProperty("qualifications") val qualifications: List<Qualification>) :
        Serializable {
        class Qualification(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: QualificationId,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("internalId") @field:JsonProperty("internalId") val internalId: String?,
            @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,
            @param:JsonProperty("status") @field:JsonProperty("status") val status: QualificationStatus,
            @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: QualificationStatusDetails,
            @param:JsonProperty("relatedSubmission") @field:JsonProperty("relatedSubmission") val relatedSubmission: SubmissionId,
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("requirementResponses") @field:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse>?,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("scoring") @field:JsonProperty("scoring") val scoring: Scoring?,
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?
        ) {
            data class RequirementResponse(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementResponseId,
                @JsonDeserialize(using = RequirementValueDeserializer::class)
                @JsonSerialize(using = RequirementValueSerializer::class)
                @param:JsonProperty("value") @field:JsonProperty("value") val value: RequirementResponseValue,
                @param:JsonProperty("relatedTenderer") @field:JsonProperty("relatedTenderer") val relatedTenderer: RelatedTenderer,
                @param:JsonProperty("requirement") @field:JsonProperty("requirement") val requirement: Requirement,
                @param:JsonProperty("responder") @field:JsonProperty("responder") val responder: Responder
            ) {
                data class RelatedTenderer(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: OrganizationId
                )

                data class Requirement(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementId
                )

                data class Responder(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: PersonId,
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                )
            }

            data class Document(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: DocumentId,
                @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: DocumentType,
                @param:JsonProperty("title") @field:JsonProperty("title") val title: String,
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
            )
        }
    }
}

package com.procurement.orchestrator.infrastructure.client.web.dossier.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.domain.model.organization.OrganizationId
import com.procurement.orchestrator.domain.model.organization.datail.MainEconomicActivityId
import com.procurement.orchestrator.domain.model.organization.datail.Scale
import com.procurement.orchestrator.domain.model.organization.datail.TypeOfSupplier
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionId
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionType
import com.procurement.orchestrator.domain.model.organization.person.PersonTitle
import com.procurement.orchestrator.domain.model.person.PersonId
import com.procurement.orchestrator.domain.model.requirement.RequirementId
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import com.procurement.orchestrator.domain.model.submission.SubmissionStatus
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class GetSubmissionsForTenderingAction : FunctionalAction<GetSubmissionsForTenderingAction.Params, GetSubmissionsForTenderingAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "findSubmissions"
    override val target: Target<Result> = Target.plural()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("pmd") @param:JsonProperty("pmd") val pmd: ProcurementMethodDetails,
        @field:JsonProperty("country") @param:JsonProperty("country") val country: String,
        @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: OperationTypeProcess
    )

    class Result(values: List<Submission>) : List<Result.Submission> by values, Serializable {

        class Submission(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: SubmissionId,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("requirementResponses") @field:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse>?,

            @param:JsonProperty("candidates") @field:JsonProperty("candidates") val candidates: List<Candidate>,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?,

            @param:JsonProperty("status") @field:JsonProperty("status") val status: SubmissionStatus,
            @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime
        ) : Serializable {

            data class RequirementResponse(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementResponseId,
                @param:JsonProperty("value") @field:JsonProperty("value") val value: RequirementResponseValue,
                @param:JsonProperty("requirement") @field:JsonProperty("requirement") val requirement: Requirement,
                @param:JsonProperty("relatedCandidate") @field:JsonProperty("relatedCandidate") val relatedCandidate: RelatedCandidate
            ) : Serializable {

                data class Requirement(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementId
                ) : Serializable

                data class RelatedCandidate(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: OrganizationId,
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                ) : Serializable
            }

            data class Candidate(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: OrganizationId,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
                @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("additionalIdentifiers") @field:JsonProperty("additionalIdentifiers") val additionalIdentifiers: List<AdditionalIdentifier>?,

                @param:JsonProperty("address") @field:JsonProperty("address") val address: Address,
                @param:JsonProperty("contactPoint") @field:JsonProperty("contactPoint") val contactPoint: ContactPoint,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("persones") @field:JsonProperty("persones") val persones: List<Persone>?,

                @param:JsonProperty("details") @field:JsonProperty("details") val details: Details
            ) : Serializable {

                data class Identifier(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("legalName") @field:JsonProperty("legalName") val legalName: String,
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                ) : Serializable

                data class AdditionalIdentifier(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("legalName") @field:JsonProperty("legalName") val legalName: String,
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                ) : Serializable

                data class Address(
                    @param:JsonProperty("streetAddress") @field:JsonProperty("streetAddress") val streetAddress: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("postalCode") @field:JsonProperty("postalCode") val postalCode: String?,

                    @param:JsonProperty("addressDetails") @field:JsonProperty("addressDetails") val addressDetails: AddressDetails
                ) : Serializable {
                    data class AddressDetails(
                        @param:JsonProperty("country") @field:JsonProperty("country") val country: Country,
                        @param:JsonProperty("region") @field:JsonProperty("region") val region: Region,
                        @param:JsonProperty("locality") @field:JsonProperty("locality") val locality: Locality
                    ) : Serializable {

                        data class Country(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                        ) : Serializable

                        data class Region(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                        ) : Serializable

                        data class Locality(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                        ) : Serializable
                    }
                }

                data class ContactPoint(
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
                    @param:JsonProperty("email") @field:JsonProperty("email") val email: String,
                    @param:JsonProperty("telephone") @field:JsonProperty("telephone") val telephone: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("faxNumber") @field:JsonProperty("faxNumber") val faxNumber: String?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("url") @field:JsonProperty("url") val url: String?
                ) : Serializable

                data class Persone(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: PersonId,
                    @param:JsonProperty("title") @field:JsonProperty("title") val title: PersonTitle,
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
                    @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,
                    @param:JsonProperty("businessFunctions") @field:JsonProperty("businessFunctions") val businessFunctions: List<BusinessFunction>
                ) : Serializable {

                    data class Identifier(
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                    ) : Serializable

                    data class BusinessFunction(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: BusinessFunctionId,
                        @param:JsonProperty("type") @field:JsonProperty("type") val type: BusinessFunctionType,
                        @param:JsonProperty("jobTitle") @field:JsonProperty("jobTitle") val jobTitle: String,
                        @param:JsonProperty("period") @field:JsonProperty("period") val period: Period,

                        @JsonInclude(JsonInclude.Include.NON_EMPTY)
                        @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?
                    ) : Serializable {

                        data class Period(
                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime?
                        ) : Serializable

                        data class Document(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: DocumentId,
                            @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: DocumentType,
                            @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
                        ) : Serializable
                    }
                }

                data class Details(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("typeOfSupplier") @field:JsonProperty("typeOfSupplier") val typeOfSupplier: TypeOfSupplier?,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("mainEconomicActivities") @field:JsonProperty("mainEconomicActivities") val mainEconomicActivities: List<MainEconomicActivity>?,
                    @param:JsonProperty("scale") @field:JsonProperty("scale") val scale: Scale,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("bankAccounts") @field:JsonProperty("bankAccounts") val bankAccounts: List<BankAccount>?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("legalForm") @field:JsonProperty("legalForm") val legalForm: LegalForm?
                ) : Serializable {

                    data class MainEconomicActivity(
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: MainEconomicActivityId,
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                    ) : Serializable

                    data class BankAccount(
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                        @param:JsonProperty("bankName") @field:JsonProperty("bankName") val bankName: String,
                        @param:JsonProperty("address") @field:JsonProperty("address") val address: Address,
                        @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,
                        @param:JsonProperty("accountIdentification") @field:JsonProperty("accountIdentification") val accountIdentification: AccountIdentification,

                        @JsonInclude(JsonInclude.Include.NON_EMPTY)
                        @param:JsonProperty("additionalAccountIdentifiers") @field:JsonProperty("additionalAccountIdentifiers") val additionalAccountIdentifiers: List<AdditionalAccountIdentifier>?
                    ) : Serializable {

                        data class Address(
                            @param:JsonProperty("streetAddress") @field:JsonProperty("streetAddress") val streetAddress: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("postalCode") @field:JsonProperty("postalCode") val postalCode: String?,

                            @param:JsonProperty("addressDetails") @field:JsonProperty("addressDetails") val addressDetails: AddressDetails
                        ) : Serializable {

                            data class AddressDetails(
                                @param:JsonProperty("country") @field:JsonProperty("country") val country: Country,
                                @param:JsonProperty("region") @field:JsonProperty("region") val region: Region,
                                @param:JsonProperty("locality") @field:JsonProperty("locality") val locality: Locality
                            ) : Serializable {

                                data class Country(
                                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                                ) : Serializable

                                data class Region(
                                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                                ) : Serializable

                                data class Locality(
                                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                                ) : Serializable
                            }
                        }

                        data class Identifier(
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                        ) : Serializable

                        data class AccountIdentification(
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                        ) : Serializable

                        data class AdditionalAccountIdentifier(
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                        ) : Serializable
                    }

                    data class LegalForm(
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                    ) : Serializable
                }
            }

            data class Document(
                @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: DocumentType,
                @param:JsonProperty("id") @field:JsonProperty("id") val id: DocumentId,
                @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
            ) : Serializable
        }
    }
}

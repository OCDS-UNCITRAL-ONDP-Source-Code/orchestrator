package com.procurement.orchestrator.infrastructure.client.web.dossier.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import com.procurement.orchestrator.infrastructure.model.Version

abstract class CheckAccessToSubmissionAction : ProceduralAction<CheckAccessToSubmissionAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "checkAccessToSubmission"

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("submissionId") @param:JsonProperty("submissionId") val submissionId: SubmissionId,
        @field:JsonProperty("token") @param:JsonProperty("token") val token: Token,
        @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: Owner
    )
}
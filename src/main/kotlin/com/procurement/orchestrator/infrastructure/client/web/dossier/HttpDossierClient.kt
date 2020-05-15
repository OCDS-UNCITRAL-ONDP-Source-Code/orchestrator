package com.procurement.orchestrator.infrastructure.client.web.dossier

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.DossierClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetSubmissionStateByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.ValidateRequirementResponseAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpDossierClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    DossierClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun validateRequirementResponse(
        id: CommandId,
        params: ValidateRequirementResponseAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = DossierCommands.ValidateRequirementResponse.build(id = id, params = params)
    )

    override suspend fun getSubmissionStateByIds(
        id: CommandId,
        params: GetSubmissionStateByIdsAction.Params
    ): Result<Reply<GetSubmissionStateByIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = DossierCommands.GetSubmissionStateByIds.build(id = id, params = params),
        target = DossierCommands.GetSubmissionStateByIds.target
    )
}

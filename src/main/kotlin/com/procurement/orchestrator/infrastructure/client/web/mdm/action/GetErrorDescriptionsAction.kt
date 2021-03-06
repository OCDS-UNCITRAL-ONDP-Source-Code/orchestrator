package com.procurement.orchestrator.infrastructure.client.web.mdm.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class GetErrorDescriptionsAction :
    FunctionalAction<GetErrorDescriptionsAction.Params, GetErrorDescriptionsAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getErrorDescription"
    override val target: Target<Result> = Target.plural()

    class Params(
        @field:JsonProperty("language") @param:JsonProperty("language") val language: String,
        @field:JsonProperty("codes") @param:JsonProperty("codes") val codes: List<String>
    ) : Serializable

    class Result(values: List<Error>) : List<Result.Error> by values, Serializable {

        class Error(
            @field:JsonProperty("code") @param:JsonProperty("code") val code: String,
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String
        ) : Serializable
    }
}

package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.LotStatus
import com.procurement.orchestrator.domain.model.lot.LotStatusDetails
import com.procurement.orchestrator.infrastructure.client.web.Target
import java.io.Serializable

abstract class GetLotIdsAction : FunctionalAction<GetLotIdsAction.Params, GetLotIdsAction.Result> {
    override val version: String = "2.0.0"
    override val name: String = "getLotIds"

    override val target: Target<Result> =
        Target.Plural(typeRef = Result::class.java, defaultResult = { Result(emptyList<LotId.Permanent>()) })

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("states") @param:JsonProperty("states") val states: List<State>
    ) {

        class State(
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("status") @param:JsonProperty("status") val status: LotStatus? = null,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: LotStatusDetails? = null
        )
    }

    class Result(values: List<LotId.Permanent>) : List<LotId> by values, Serializable
}

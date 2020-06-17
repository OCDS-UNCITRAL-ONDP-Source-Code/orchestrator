package com.procurement.orchestrator.domain.model.address.locality

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject

import com.procurement.orchestrator.domain.model.or
import java.io.Serializable

data class LocalityDetails(
    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: LocalityScheme,
    @field:JsonProperty("id") @param:JsonProperty("id") val id: LocalityId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String? = null
) : IdentifiableObject<LocalityDetails>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is LocalityDetails
            && this.scheme.toUpperCase() == other.scheme.toUpperCase()
            && this.id.toUpperCase() == other.id.toUpperCase()

    override fun hashCode(): Int {
        var result = scheme.toUpperCase().hashCode()
        result = 31 * result + id.toUpperCase().hashCode()
        return result
    }

    override fun updateBy(src: LocalityDetails) = LocalityDetails(
        scheme = src.scheme,
        id = src.id,
        description = src.description or description,
        uri = src.uri or uri
    )
}

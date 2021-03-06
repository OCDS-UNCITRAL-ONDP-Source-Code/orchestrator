package com.procurement.orchestrator.domain.model.organization.datail.account

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.address.Address
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.updateBy
import java.io.Serializable

data class BankAccount(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("bankName") @param:JsonProperty("bankName") val bankName: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("address") @param:JsonProperty("address") val address: Address? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: AccountIdentifier? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("accountIdentification") @param:JsonProperty("accountIdentification") val accountIdentification: AccountIdentifier? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("additionalAccountIdentifiers") @param:JsonProperty("additionalAccountIdentifiers") val additionalAccountIdentifiers: AccountIdentifiers = AccountIdentifiers()
) : ComplexObject<BankAccount>, Serializable {

    override fun updateBy(src: BankAccount) = BankAccount(
        description = src.description or description,
        bankName = src.bankName or bankName,
        address = address updateBy src.address,
        identifier = identifier updateBy src.identifier,
        accountIdentification = accountIdentification updateBy src.accountIdentification,
        additionalAccountIdentifiers = additionalAccountIdentifiers updateBy src.additionalAccountIdentifiers
    )
}

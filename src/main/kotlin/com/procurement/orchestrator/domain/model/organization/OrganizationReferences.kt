package com.procurement.orchestrator.domain.model.organization

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class OrganizationReferences(
    values: List<OrganizationReference> = emptyList()
) : List<OrganizationReference> by values,
    IdentifiableObjects<OrganizationReference, OrganizationReferences>,
    Serializable {

    constructor(value: OrganizationReference) : this(listOf(value))

    override operator fun plus(other: OrganizationReferences) =
        OrganizationReferences(this as List<OrganizationReference> + other as List<OrganizationReference>)

    override operator fun plus(others: List<OrganizationReference>) =
        OrganizationReferences(this as List<OrganizationReference> + others)

    override fun updateBy(src: OrganizationReferences) = OrganizationReferences(update(dst = this, src = src))
}

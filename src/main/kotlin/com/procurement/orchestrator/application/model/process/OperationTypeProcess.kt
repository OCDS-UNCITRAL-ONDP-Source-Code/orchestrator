package com.procurement.orchestrator.application.model.process

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class OperationTypeProcess(@JsonValue override val key: String) : EnumElementProvider.Key {

    DECLARE_NON_CONFLICT_OF_INTEREST("declareNonConflictOfInterest"),
    LOT_CANCELLATION("lotCancellation"),
    TENDER_CANCELLATION("tenderCancellation"),
    TENDER_OR_LOT_AMENDMENT_CANCELLATION("tenderOrLotAmendmentCancellation"),
    TENDER_OR_LOT_AMENDMENT_CONFIRMATION("tenderOrLotAmendmentConfirmation");

    override fun toString(): String = key

    companion object : EnumElementProvider<OperationTypeProcess>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}

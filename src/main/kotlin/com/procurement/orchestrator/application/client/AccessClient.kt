package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckAccessToTenderAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckPersonesStructureAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckTenderStateAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CreateCriteriaForProcuringEntityAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.FindCriteriaAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.FindLotIdsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetLotStateByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetOrganizationAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetQualificationCriteriaAndMethodAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetTenderStateAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.ResponderProcessingAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.SetStateForLotsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.SetStateForTenderAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.ValidateRequirementResponsesAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.VerifyRequirementResponseAction

interface AccessClient {

    suspend fun checkAccessToTender(
        id: CommandId,
        params: CheckAccessToTenderAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    @Deprecated(message = "Using validateRequirementResponses")
    suspend fun checkPersonsStructure(
        id: CommandId,
        params: CheckPersonesStructureAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun verifyRequirementResponse(
        id: CommandId,
        params: VerifyRequirementResponseAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun findLotIds(
        id: CommandId,
        params: FindLotIdsAction.Params
    ): Result<Reply<FindLotIdsAction.Result>, Fail.Incident>

    suspend fun getLotStateByIds(
        id: CommandId,
        params: GetLotStateByIdsAction.Params
    ): Result<Reply<GetLotStateByIdsAction.Result>, Fail.Incident>

    suspend fun responderProcessing(
        id: CommandId,
        params: ResponderProcessingAction.Params
    ): Result<Reply<ResponderProcessingAction.Result>, Fail.Incident>

    suspend fun setStateForTender(
        id: CommandId,
        params: SetStateForTenderAction.Params
    ): Result<Reply<SetStateForTenderAction.Result>, Fail.Incident>

    suspend fun getTenderState(
        id: CommandId,
        params: GetTenderStateAction.Params
    ): Result<Reply<GetTenderStateAction.Result>, Fail.Incident>

    suspend fun setStateForLots(
        id: CommandId,
        params: SetStateForLotsAction.Params
    ): Result<Reply<SetStateForLotsAction.Result>, Fail.Incident>

    suspend fun getOrganization(
        id: CommandId,
        params: GetOrganizationAction.Params
    ): Result<Reply<GetOrganizationAction.Result>, Fail.Incident>

    suspend fun createCriteriaForProcuringEntity(
        id: CommandId,
        params: CreateCriteriaForProcuringEntityAction.Params
    ): Result<Reply<CreateCriteriaForProcuringEntityAction.Result>, Fail.Incident>

    suspend fun getQualificationCriteriaAndMethod(
        id: CommandId,
        params: GetQualificationCriteriaAndMethodAction.Params
    ): Result<Reply<GetQualificationCriteriaAndMethodAction.Result>, Fail.Incident>

    suspend fun validateRequirementResponses(
        id: CommandId,
        params: ValidateRequirementResponsesAction.Params
    ): Result<Reply<ValidateRequirementResponsesAction.Result>, Fail.Incident>

    suspend fun findCriteria(
        id: CommandId,
        params: FindCriteriaAction.Params
    ): Result<Reply<FindCriteriaAction.Result>, Fail.Incident>

    suspend fun checkTenderState(
        id: CommandId,
        params: CheckTenderStateAction.Params
    ): Result<Reply<Unit>, Fail.Incident>
}

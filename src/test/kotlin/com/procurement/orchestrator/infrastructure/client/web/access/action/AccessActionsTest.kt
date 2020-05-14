package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AccessActionsTest {

    @Nested
    inner class CheckAccessToTender {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckAccessToTenderAction.Params>("json/client/access/check_access_to_tender_params_full.json")
            }
        }
    }

    @Nested
    inner class VerifyRequirementResponse {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<VerifyRequirementResponseAction.Params>("json/client/access/verify_requirement_response_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<VerifyRequirementResponseAction.Params>("json/client/access/verify_requirement_response_params_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<VerifyRequirementResponseAction.Params>("json/client/access/verify_requirement_response_params_required_2.json")
            }
        }
    }

    @Nested
    inner class GetLotStateByIds {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetLotStateByIdsAction.Params>("json/client/access/get_lot_state_by_ids_action_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetLotStateByIdsAction.Result>("json/client/access/get_lot_state_by_ids_action_result_full.json")
            }
        }
    }

    @Nested
    inner class FindLotIdsActionTest {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<FindLotIdsAction.Params>("json/client/access/find_lot_ids_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<FindLotIdsAction.Result>("json/client/access/find_lot_ids_result_full.json")
            }
        }
    }

    @Nested
    inner class ResponderProcessing {

        @Nested
        inner class Params {

            @Test
            fun fully() {
                testingBindingAndMapping<ResponderProcessingAction.Params>("json/client/access/responder_processing_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<ResponderProcessingAction.Params>("json/client/access/responder_processing_params_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<ResponderProcessingAction.Params>("json/client/access/responder_processing_params_required_2.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<ResponderProcessingAction.Result>("json/client/access/responder_processing_result_full.json")
            }
        }
    }

    @Nested
    inner class SetStateForTender {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<SetStateForTenderAction.Params>("json/client/access/set_state_for_tender_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<SetStateForTenderAction.Result>("json/client/access/set_state_for_tender_result_full.json")
            }
        }
    }

    @Nested
    inner class SetStateForLots {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<SetStateForLotsAction.Params>("json/client/access/set_state_for_lots_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<SetStateForLotsAction.Result>("json/client/access/set_state_for_lots_result_full.json")
            }
        }
    }

    @Nested
    inner class GetTenderState {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetTenderStateAction.Params>("json/client/access/get_tender_state_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetTenderStateAction.Result>("json/client/access/get_tender_state_result_full.json")
            }
        }
    }


    @Nested
    inner class GetOrganization {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetOrganizationAction.Params>("json/client/access/get_organization_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetOrganizationAction.Result>("json/client/access/get_organization_result_full.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<GetOrganizationAction.Result>("json/client/access/get_organization_result_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<GetOrganizationAction.Result>("json/client/access/get_organization_result_required_2.json")
            }

            @Test
            fun required_3() {
                testingBindingAndMapping<GetOrganizationAction.Result>("json/client/access/get_organization_result_required_3.json")
            }
        }
    }
}

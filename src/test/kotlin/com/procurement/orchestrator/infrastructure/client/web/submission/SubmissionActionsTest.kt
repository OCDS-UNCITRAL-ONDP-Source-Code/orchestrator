package com.procurement.orchestrator.infrastructure.client.web.submission

import com.procurement.orchestrator.infrastructure.client.web.submission.action.CheckAbsenceActiveInvitationsAction
import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SubmissionActionsTest {

    @Nested
    inner class CheckAbsenceActiveInvitations {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckAbsenceActiveInvitationsAction.Params>("json/client/submission/check_absence_active_invitations_params_full.json")
            }
        }
    }
}


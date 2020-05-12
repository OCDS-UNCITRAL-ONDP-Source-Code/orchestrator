package com.procurement.orchestrator.infrastructure.client.web.qualification

import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckAccessToSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckPeriodAction

object QualificationCommands {

    object CheckPeriod : CheckPeriodAction()

    object CheckAccessToSubmission: CheckAccessToSubmissionAction()
}

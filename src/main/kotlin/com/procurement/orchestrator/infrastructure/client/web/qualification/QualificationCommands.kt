package com.procurement.orchestrator.infrastructure.client.web.qualification

import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CheckPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CreateSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.ValidateSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.FindQualificationIdsAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.GetNextsForQualificationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.StartQualificationPeriodAction

object QualificationCommands {

    object CreateSubmission : CreateSubmissionAction()

    object CheckPeriod : CheckPeriodAction()

    object ValidateSubmission : ValidateSubmissionAction()

    object StartQualificationPeriod: StartQualificationPeriodAction()

    object FindQualificationIds : FindQualificationIdsAction()

    object GetNextsForQualification : GetNextsForQualificationAction()
}

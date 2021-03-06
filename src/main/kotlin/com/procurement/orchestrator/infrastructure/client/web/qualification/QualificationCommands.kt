package com.procurement.orchestrator.infrastructure.client.web.qualification

import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CheckPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CreateSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.ValidateSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.AnalyzeQualificationForInvitationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckAccessToQualificationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckDeclarationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckQualificationForProtocolAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckQualificationPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckQualificationStateAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CreateQualificationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.DoConsiderationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.DoDeclarationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.DoQualificationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.FinalizeQualificationsAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.FindQualificationIdsAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.FindRequirementResponseByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.RankQualificationsAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.SetNextForQualificationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.SetQualificationPeriodEndAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.StartQualificationPeriodAction

object QualificationCommands {

    object CreateSubmission : CreateSubmissionAction()

    object CheckPeriod : CheckPeriodAction()

    object ValidateSubmission : ValidateSubmissionAction()

    object StartQualificationPeriod : StartQualificationPeriodAction()

    object FindQualificationIds : FindQualificationIdsAction()

    object CheckDeclaration : CheckDeclarationAction()

    object CreateQualification : CreateQualificationAction()

    object RankQualifications : RankQualificationsAction()

    object CheckQualificationState : CheckQualificationStateAction()

    object DoDeclaration : DoDeclarationAction()

    object FindRequirementResponseByIds: FindRequirementResponseByIdsAction()

    object CheckAccessToQualification : CheckAccessToQualificationAction()

    object DoConsideration: DoConsiderationAction()

    object DoQualification: DoQualificationAction()

    object SetNextForQualification: SetNextForQualificationAction()

    object AnalyzeQualificationForInvitation : AnalyzeQualificationForInvitationAction()

    object CheckQualificationForProtocol : CheckQualificationForProtocolAction()

    object FinalizeQualifications : FinalizeQualificationsAction()

    object CheckQualificationPeriod: CheckQualificationPeriodAction()

   object SetQualificationPeriodEnd: SetQualificationPeriodEndAction()
}

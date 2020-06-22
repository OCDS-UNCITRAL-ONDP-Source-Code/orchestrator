package com.procurement.orchestrator.infrastructure.client.web.qualification.action

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class QualificationActionTest {

    @Nested
    inner class StartQualificationPeriod {
        @Nested
        inner class Params {
            @Test
            fun required() {
                testingBindingAndMapping<StartQualificationPeriodAction.Params>("json/client/qualification/start_qualification_period_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun required() {
                testingBindingAndMapping<StartQualificationPeriodAction.Result>("json/client/qualification/start_qualification_period_result_full.json")
            }
        }
    }

    @Nested
    inner class FindQualificationIds {

        @Nested
        inner class Params {
            @Test
            fun required() {
                testingBindingAndMapping<FindQualificationIdsAction.Params>("json/client/qualification/find_qualification_ids_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<FindQualificationIdsAction.Params>("json/client/qualification/find_qualification_ids_params_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<FindQualificationIdsAction.Params>("json/client/qualification/find_qualification_ids_params_2.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun required() {
                testingBindingAndMapping<FindQualificationIdsAction.Result>("json/client/qualification/find_qualification_ids_result_full.json")
            }
        }

        @Nested
        inner class CheckDeclaration {
            @Nested
            inner class Params {
                @Test
                fun required() {
                    testingBindingAndMapping<CheckDeclarationAction.Params>("json/client/qualification/check_declataion_params_full.json")
                }
            }
        }

        @Nested
        inner class DoDeclaration {
            @Nested
            inner class Params {
                @Test
                fun required() {
                    testingBindingAndMapping<DoDeclarationAction.Params>("json/client/qualification/do_declaration_params_full.json")
                }
            }

            @Nested
            inner class Result {
                @Test
                fun required() {
                    testingBindingAndMapping<DoDeclarationAction.Result>("json/client/qualification/do_declaration_result_full.json")
                }
            }
        }
    }
}

package com.procurement.orchestrator.infrastructure.client.web.mdm

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.MdmClient
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractRestDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.mdm.MdmEnrichLocalityDelegate
import com.procurement.orchestrator.infrastructure.bpms.repository.ErrorDescriptionRepository
import com.procurement.orchestrator.infrastructure.client.reply.EMPTY_REPLY_ID
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.CallResponse
import com.procurement.orchestrator.infrastructure.client.web.RestClient
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichCountryAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichLocalityAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichRegionAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetCountry
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetErrorDescriptionsAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetLocality
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRegion
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import com.procurement.orchestrator.infrastructure.model.Version
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.net.URL

class HttpMdmClient(
    private val errorDescriptionRepository: ErrorDescriptionRepository,
    private val restClient: RestClient,
    private val transform: Transform,
    properties: ComponentProperties.Component
) : MdmClient {

    private val baseUrl: URL = URL(properties.url)

    override suspend fun getErrorDescription(
        id: CommandId,
        params: GetErrorDescriptionsAction.Params
    ): Result<Reply<GetErrorDescriptionsAction.Result>, Fail.Incident> =

        errorDescriptionRepository.load(codes = params.codes, language = params.language.toUpperCase())
            .orForwardFail { fail -> return fail }
            .map { error ->
                GetErrorDescriptionsAction.Result.Error(
                    code = error.code,
                    description = error.description
                )
            }
            .let { errors ->
                Reply.Success(
                    id = EMPTY_REPLY_ID,
                    version = Version.parse("2.0.0"),
                    result = Option.pure(GetErrorDescriptionsAction.Result(errors))
                )
            }
            .asSuccess()

    override suspend fun enrichCountry(
        id: CommandId,
        params: EnrichCountryAction.Params
    ): Result<GetCountry.Result, Fail.Incident> {

        val httpUrl: HttpUrl = getCountryEndpoint(params)
            .toHttpUrl()
            .newBuilder()
            .apply {
                addQueryParameter("lang", params.lang)
                addQueryParameter("scheme", params.scheme)
            }
            .build()

        val response = restClient.call(url = httpUrl)
            .orForwardFail { error -> return error }

        return processGetCountryResponse(response, transform)
    }

    private fun getCountryEndpoint(params: EnrichCountryAction.Params): String = "$baseUrl/addresses/countries/${params.countyId}"

    override suspend fun enrichRegion(
        id: CommandId,
        params: EnrichRegionAction.Params
    ): Result<GetRegion.Result, Fail.Incident> {

        val httpUrl: HttpUrl = getRegionEndpoint(params)
            .toHttpUrl()
            .newBuilder()
            .apply {
                addQueryParameter("lang", params.lang)
                addQueryParameter("scheme", params.scheme)
            }
            .build()

        val response = restClient.call(url = httpUrl)
            .orForwardFail { error -> return error }

        return processGetRegionResponse(response, transform)
    }

    private fun getRegionEndpoint(params: EnrichRegionAction.Params): String =
        "$baseUrl/addresses/countries/${params.countyId}/regions/${params.regionId}"

    override suspend fun enrichLocality(
        id: CommandId,
        params: EnrichLocalityAction.Params
    ): Result<GetLocality.Result, Fail.Incident> {

        val httpUrl: HttpUrl = getLocalityEndpoint(params)
            .toHttpUrl()
            .newBuilder()
            .apply {
                addQueryParameter("lang", params.lang)
                addQueryParameter("scheme", params.scheme)
            }
            .build()

        val response = restClient.call(url = httpUrl)
            .orForwardFail { error -> return error }

        return processGetLocalityResponse(response, transform)
    }

    private fun getLocalityEndpoint(params: EnrichLocalityAction.Params): String =
        "$baseUrl/addresses/countries/${params.countyId}/regions/${params.regionId}/localities/${params.localityId}"

    private fun processGetCountryResponse(
        response: CallResponse,
        transform: Transform
    ): Result<GetCountry.Result, Fail.Incident> = when (response.code) {
        AbstractRestDelegate.HTTP_CODE_200 -> transform
            .tryDeserialization(
                value = response.content,
                target = EnrichCountryAction.Response.Success::class.java
            )
            .orReturnFail { fail ->
                return failure(
                    Fail.Incident.BadResponse(description = fail.description, body = response.content)
                )
            }
            .let { result ->
                GetCountry.Result.Success(
                    id = result.data.id,
                    description = result.data.description,
                    uri = result.data.uri,
                    scheme = result.data.scheme
                )
            }
            .asSuccess()

        AbstractRestDelegate.HTTP_CODE_404 -> transform
            .tryDeserialization(
                value = response.content,
                target = EnrichCountryAction.Response.Error::class.java
            )
            .orForwardFail { error -> return error }
            .let { responseError ->
                GetCountry.Result.Fail(
                    errors = responseError.errors
                        .map { error ->
                            GetCountry.Result.Fail.Error(
                                code = error.code,
                                description = error.description
                            )
                        }
                )
            }
            .asSuccess()

        else                               -> failure(
            Fail.Incident.BadResponse(
                description = "Invalid response code.",
                body = response.content
            )
        )
    }

    private fun processGetRegionResponse(
        response: CallResponse,
        transform: Transform
    ): Result<GetRegion.Result, Fail.Incident> = when (response.code) {
        AbstractRestDelegate.HTTP_CODE_200 -> transform
            .tryDeserialization(
                value = response.content,
                target = EnrichRegionAction.Response.Success::class.java
            )
            .orReturnFail { fail ->
                return failure(
                    Fail.Incident.BadResponse(description = fail.description, body = response.content)
                )
            }
            .let { result ->
                GetRegion.Result.Success(
                    id = result.data.id,
                    description = result.data.description,
                    uri = result.data.uri,
                    scheme = result.data.scheme
                )
            }
            .asSuccess()

        AbstractRestDelegate.HTTP_CODE_404 -> transform
            .tryDeserialization(
                value = response.content,
                target = EnrichRegionAction.Response.Error::class.java
            )
            .orForwardFail { error -> return error }
            .let { responseError ->
                GetRegion.Result.Fail(
                    errors = responseError.errors
                        .map { error ->
                            GetRegion.Result.Fail.Error(
                                code = error.code,
                                description = error.description
                            )
                        }
                )
            }
            .asSuccess()

        else                               -> failure(
            Fail.Incident.BadResponse(
                description = "Invalid response code.",
                body = response.content
            )
        )
    }

    private fun processGetLocalityResponse(
        response: CallResponse,
        transform: Transform
    ): Result<GetLocality.Result, Fail.Incident> = when (response.code) {
        AbstractRestDelegate.HTTP_CODE_200 -> transform
            .tryDeserialization(
                value = response.content,
                target = EnrichLocalityAction.Response.Success::class.java
            )
            .orReturnFail { fail ->
                return failure(
                    Fail.Incident.BadResponse(description = fail.description, body = response.content)
                )
            }
            .let { result ->
                GetLocality.Result.Success(
                    id = result.data.id,
                    description = result.data.description,
                    uri = result.data.uri,
                    scheme = result.data.scheme
                )
            }
            .asSuccess()

        AbstractRestDelegate.HTTP_CODE_404 -> transform
            .tryDeserialization(
                value = response.content,
                target = EnrichLocalityAction.Response.Error::class.java
            )
            .orForwardFail { error -> return error }
            .let { responseError ->
                val error = responseError.errors.first()
                when (error.code) {
                    MdmEnrichLocalityDelegate.RESPONSE_SCHEME_NOT_FOUND ->
                        success(GetLocality.Result.Fail.SchemeNotFound)
                    MdmEnrichLocalityDelegate.RESPONSE_ID_NOT_FOUND     ->
                        success(GetLocality.Result.Fail.IdNotFound(responseError))
                    else                                                ->
                        failure(Fail.Incident.BadResponse(description = "Unknown error code.", body = response.content))
                }
            }

        else  ->  failure(Fail.Incident.BadResponse(description = "Invalid response code.", body = response.content))

    }
}

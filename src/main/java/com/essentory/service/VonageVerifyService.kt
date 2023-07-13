package com.essentory.service

import com.essentory.dto.verify.VerifyDto
import com.essentory.dto.verify.VerifyReq
import com.essentory.dto.verify.VerifyRes
import com.essentory.exceptions.BlackListNumberException
import com.essentory.exceptions.CriticalStatusCodeException
import com.essentory.exceptions.RestrictedCountryException
import com.essentory.exceptions.VonageException
import com.vonage.client.VonageClient
import com.vonage.client.verify.VerifyRequest
import com.vonage.client.verify.VerifyResponse
import com.vonage.client.verify.VerifyStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service


@Service
class VonageVerifyService (
    val vonageClient: VonageClient,
) : VerifyService  {

    @Value("\${vonage_auth.brand_name}")
    private val brandName: String? = "brand_name"

    @Value("\${vonage_auth.expiry}")
    private val expiry: String? = "180"

    override fun verifyPhone(verifyReq: VerifyReq): VerifyDto<VerifyRes> {
        try {
            val response = vonageClient.verifyClient.verify(
                    VerifyRequest.builder(verifyReq.phoneNumber(), brandName)
                        .pinExpiry(expiry?.toExpirySeconds())
                        .workflow(VerifyRequest.Workflow.SMS)
                        .build())

            checkBlacklist(response, verifyReq.phoneNumber())

            checkCriticalStatusCode(response)

            verifyAPIAccessIsRestrictedCountry(response, verifyReq.phoneNumber())

            return VerifyDto.of(VerifyRes(
                        requestId = response?.requestId ?: "",
                        success = response?.status == VerifyStatus.OK,
                        errorText = response?.errorText ?: ""))

        }catch (vonageException: RuntimeException ) {
            throw VonageException("network, response parsing error", vonageException)
        }
    }

    override fun verifyCode(requestId: String, code: String): Boolean {
        val check = vonageClient.verifyClient.check(requestId, code)
        return check.status == VerifyStatus.OK
    }

    fun checkBlacklist(response: VerifyResponse, phoneNumber: String) {
        if(response.status !== VerifyStatus.NUMBER_BARRED) return
        throw BlackListNumberException("blacklist number", getRequestId(response))
    }

    fun verifyAPIAccessIsRestrictedCountry(response: VerifyResponse, phoneNumber: String) {
        if (response.status !== VerifyStatus.UNSUPPORTED_NETWORK) return
        throw RestrictedCountryException("Request restricted country", getRequestId(response))
    }

    fun checkCriticalStatusCode(response: VerifyResponse) {
        val criticalStatus = response.status in setOf(
                VerifyStatus.INVALID_CREDENTIALS,
                VerifyStatus.INTERNAL_ERROR,
                VerifyStatus.INVALID_REQUEST,
                VerifyStatus.PARTNER_QUOTA_EXCEEDED
        )
        if(!criticalStatus) return
        throw CriticalStatusCodeException("critical status code", getRequestId(response), response.status, response.errorText)
    }

    private fun getRequestId(response: VerifyResponse): String? = response.requestId.ifBlank { response.network }


    fun String.toExpirySeconds(default: Int = 180): Int {
        return try {
            this.toInt()
        } catch (e:NumberFormatException) {
            default
        }
    }
}

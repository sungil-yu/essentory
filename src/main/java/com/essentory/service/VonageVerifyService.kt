package com.essentory.service

import com.essentory.controller.support.VonageExceptionDto
import com.essentory.dto.verify.VerifyDto
import com.essentory.dto.verify.VerifyReq
import com.essentory.dto.verify.VerifyRes
import com.essentory.exceptions.*
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
    private val brandName: String = "brand_name"

    @Value("\${vonage_auth.expiry}")
    private val expiry: String = "180"

    override fun verifyPhone(verifyReq: VerifyReq): VerifyDto<VerifyRes> {
        try {
            val response = vonageClient.verifyClient.verify(
                    VerifyRequest.builder(verifyReq.phoneNumber(), brandName)
                        .pinExpiry(expiry.toExpirySeconds())
                        .workflow(VerifyRequest.Workflow.SMS)
                        .build())

            if(response.status !== VerifyStatus.OK){
                val vonageExceptionDto = VonageExceptionDto(response.requestId, response.status, response.errorText, response.network)

                checkBlacklist(vonageExceptionDto)

                checkCriticalStatusCode(vonageExceptionDto)

                verifyAPIAccessIsRestrictedCountry(vonageExceptionDto)
            }

            return VerifyDto.of(VerifyRes(
                        requestId = response.requestId,
                        success = response.status == VerifyStatus.OK,
                        errorText = response?.errorText ?: ""))
        }catch (vonageException: RuntimeException) {
            throw VonageException("network, response parsing error", vonageException)
        }
    }

    override fun verifyCode(requestId: String, code: String): Boolean {
        try {
            val response = vonageClient.verifyClient.check(requestId, code)

            if (response.status !== VerifyStatus.OK) {
                val vonageExceptionDto = VonageExceptionDto(response.requestId, response.status, response.errorText)

                checkCriticalStatusCode(vonageExceptionDto)

                isMismatchCode(vonageExceptionDto)

                repeatedInvalidCode(vonageExceptionDto)
            }

            return response.status == VerifyStatus.OK
        }catch (vonageException: RuntimeException) {
            throw VonageException("network, response parsing error", vonageException)
        }
    }

    private fun checkBlacklist(vonageExceptionDto: VonageExceptionDto) {
        if(vonageExceptionDto.status !== VerifyStatus.NUMBER_BARRED) return
        throw BlackListNumberException("blacklist number", vonageExceptionDto)
    }

    private fun verifyAPIAccessIsRestrictedCountry(vonageExceptionDto: VonageExceptionDto) {
        if (vonageExceptionDto.status !== VerifyStatus.UNSUPPORTED_NETWORK) return
        throw RestrictedCountryException("Request restricted country", vonageExceptionDto)
    }

    private fun checkCriticalStatusCode(vonageExceptionDto: VonageExceptionDto) {
        val isCritical = vonageExceptionDto.status in setOf(
                VerifyStatus.INVALID_CREDENTIALS,
                VerifyStatus.INTERNAL_ERROR,
                VerifyStatus.INVALID_REQUEST,
                VerifyStatus.PARTNER_QUOTA_EXCEEDED)

        if (!isCritical) return

        throw CriticalStatusCodeException("critical status code", vonageExceptionDto)
    }

    private fun isMismatchCode(vonageExceptionDto: VonageExceptionDto) {
        if (vonageExceptionDto.status !== VerifyStatus.INVALID_CODE) return
        throw VerifyCodeMismatchException("verify code mismatch", vonageExceptionDto)
    }
    private fun repeatedInvalidCode(vonageExceptionDto: VonageExceptionDto) {
        if (vonageExceptionDto.status !== VerifyStatus.WRONG_CODE_THROTTLED) return
        throw RepeatedInvalidCodeException("The wrong code was provided too many times", vonageExceptionDto)
    }

    private fun String.toExpirySeconds(default: Int = 180): Int {
        return try {
            this.toInt()
        } catch (e:NumberFormatException) {
            default
        }
    }
}

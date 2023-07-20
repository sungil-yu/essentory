package com.essentory.service

import com.essentory.controller.support.VonageExceptionDto
import com.essentory.dto.verify.VerifyReq
import com.essentory.dto.verify.VerifyRes
import com.essentory.exceptions.*
import com.vonage.client.VonageClientException
import com.vonage.client.VonageResponseParseException
import com.vonage.client.verify.CheckResponse
import com.vonage.client.verify.VerifyResponse
import com.vonage.client.verify.VerifyStatus
import org.springframework.stereotype.Component

@Component
class VonageVerifyAdapter(
        private val vonageClientWrapper: VonageClientWrapper
) {

    fun verifyPhone(verifyReq: VerifyReq): VerifyRes {
        runCatching {
            vonageClientWrapper.requestVerifyCode(verifyReq)
        }.onFailure {
            when (it) {
                is VonageClientException -> throw VonageException("vonage server network disconnected", it)
                is VonageResponseParseException -> throw VonageException("response parsing error", it)
            }
        }.getOrThrow().run {
            val result = adaptVerifyResponse(this)
            return VerifyRes(
                    requestId = result.requestId,
                    success = result.success,
                    errorText = result.errorText
            )
        }
    }

    fun check(requestId: String, code: String): VerifyRes {
        runCatching {
            vonageClientWrapper.verifyCode(requestId, code)
        }.onFailure {
            when (it) {
                is VonageClientException -> throw VonageException("vonage server network disconnected", it)
                is VonageResponseParseException -> throw VonageException("response parsing error", it)
                else -> throw VonageException("unknown error", it)
            }
        }.getOrThrow().run {
            val result = adaptCheckResponse(this)
            return VerifyRes(
                    requestId = result.requestId,
                    success = result.success,
                    errorText = result.errorText
            )
        }
    }

    private fun adaptVerifyResponse(response: VerifyResponse): VonageVerifyResult {
        val vonageExceptionDto = VonageExceptionDto(
                requestId = response.requestId,
                status = response.status,
                errorText = response.errorText,
                network = response.network
        )
        when (response.status) {
            VerifyStatus.NUMBER_BARRED -> throw BlackListNumberException("blacklist number", vonageExceptionDto)
            VerifyStatus.UNSUPPORTED_NETWORK -> throw RestrictedCountryException("Request restricted country", vonageExceptionDto)
            VerifyStatus.MISSING_PARAMS -> throw MissingParamsException("bad request params or missing params", vonageExceptionDto)
            else -> checkCriticalStatusCode(vonageExceptionDto)
        }

        return VonageVerifyResult(
                requestId = response.requestId ?: response.network ?: "",
                success = response.status == VerifyStatus.OK,
                errorText = response.errorText ?: ""
        )
    }
    private fun adaptCheckResponse(response: CheckResponse): VonageVerifyResult {
        val vonageExceptionDto = VonageExceptionDto(
                response.requestId,
                response.status,
                response.errorText
        )

        when (response.status) {
            VerifyStatus.INVALID_CODE -> throw VonageVerificationCodeMismatchException("verify code mismatch", vonageExceptionDto)
            VerifyStatus.WRONG_CODE_THROTTLED -> throw RepeatedInvalidCodeException("The wrong code was provided too many times", vonageExceptionDto)
            else -> checkCriticalStatusCode(vonageExceptionDto)
        }

        return VonageVerifyResult(
                requestId = response.requestId ?: "",
                success = response.status == VerifyStatus.OK,
                errorText = response.errorText ?: ""
        )
    }
    private fun checkCriticalStatusCode(vonageExceptionDto: VonageExceptionDto) {
        val isCritical = vonageExceptionDto.status in setOf(
                VerifyStatus.INVALID_CREDENTIALS,
                VerifyStatus.INTERNAL_ERROR,
                VerifyStatus.PARTNER_QUOTA_EXCEEDED
        )

        if (isCritical) {
            throw CriticalStatusCodeException("critical status code: ${vonageExceptionDto.status}", vonageExceptionDto)
        }
    }
}

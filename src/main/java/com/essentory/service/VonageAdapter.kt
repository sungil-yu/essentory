package com.essentory.service

import com.essentory.controller.support.VonageExceptionDto
import com.essentory.exceptions.*
import com.vonage.client.verify.CheckResponse
import com.vonage.client.verify.VerifyResponse
import com.vonage.client.verify.VerifyStatus

class VonageAdapter {
    companion object {
        fun adaptVerifyResponse(response: VerifyResponse): VonageVerifyResult {
            val vonageExceptionDto = VonageExceptionDto(
                    requestId = response.requestId,
                    status = response.status,
                    errorText = response.errorText,
                    network = response.network
            )

            when (response.status) {
                VerifyStatus.NUMBER_BARRED -> throw BlackListNumberException("blacklist number", vonageExceptionDto)
                VerifyStatus.UNSUPPORTED_NETWORK -> throw RestrictedCountryException("Request restricted country", vonageExceptionDto)
                else -> checkCriticalStatusCode(vonageExceptionDto)
            }

            return VonageVerifyResult(
                    requestId = response.requestId.ifBlank { response.network },
                    success = response.status == VerifyStatus.OK,
                    errorText = response.errorText ?: ""
            )
        }

        fun adaptCheckResponse(response: CheckResponse): VonageVerifyResult {
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
}

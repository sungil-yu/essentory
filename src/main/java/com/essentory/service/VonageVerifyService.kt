package com.essentory.service

import com.essentory.controller.support.VonageExceptionDto
import com.essentory.dto.verify.VerifyDto
import com.essentory.dto.verify.VerifyReq
import com.essentory.dto.verify.VerifyRes
import com.essentory.exceptions.*
import com.vonage.client.VonageClient
import com.vonage.client.VonageClientException
import com.vonage.client.VonageResponseParseException
import com.vonage.client.verify.CheckResponse
import com.vonage.client.verify.VerifyRequest
import com.vonage.client.verify.VerifyResponse
import com.vonage.client.verify.VerifyStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service



@Service
class VonageVerifyService(
        val vonageClient: VonageClient,
) : VerifyService {

    @Value("\${vonage_auth.brand_name}")
    private val brandName: String = "brand_name"

    @Value("\${vonage_auth.expiry}")
    private val expiry: String = "180"

    override fun verifyPhone(verifyReq: VerifyReq): VerifyRes {
        runCatching {
            vonageClient.verifyClient.verify(createVerifyRequest(verifyReq))
        }.onFailure {
            when (it) {
                is VonageClientException -> throw VonageException("vonage server network disconnected", it)
                is VonageResponseParseException -> throw VonageException("response parsing error", it)
                else -> throw it
            }
        }.getOrThrow().run {
            val verifyResult = VonageAdapter.adaptVerifyResponse(this)
            return VerifyRes(
                    requestId = verifyResult.requestId,
                    success = verifyResult.success,
                    errorText = verifyResult.errorText)
        }
    }
    override fun verifyCode(requestId: String, code: String): VerifyRes {
        runCatching {
            vonageClient.verifyClient.check(requestId, code)
        }.onFailure {
            when (it) {
                is VonageClientException -> throw VonageException("vonage server network disconnected", it)
                is VonageResponseParseException -> throw VonageException("response parsing error", it)
                else -> throw it
            }
        }.getOrThrow().run {
            val verifyResult = VonageAdapter.adaptCheckResponse(this)
            return VerifyRes(
                    requestId = verifyResult.requestId,
                    success = verifyResult.success,
                    errorText = verifyResult.errorText)
        }
    }
    private fun createVerifyRequest(verifyReq: VerifyReq): VerifyRequest {
        return VerifyRequest.builder(verifyReq.phoneNumber(), brandName)
                .pinExpiry(expiry.toExpirySeconds())
                .workflow(VerifyRequest.Workflow.SMS)
                .build()
    }
    private fun String.toExpirySeconds(default: Int = 180): Int {
        return runCatching {
            this.toInt()
        }.getOrDefault(default)
    }
}

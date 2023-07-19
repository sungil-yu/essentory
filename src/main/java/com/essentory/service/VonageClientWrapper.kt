package com.essentory.service

import com.essentory.dto.verify.VerifyReq
import com.essentory.exceptions.VonageException
import com.vonage.client.VonageClient
import com.vonage.client.VonageClientException
import com.vonage.client.VonageResponseParseException
import com.vonage.client.verify.CheckResponse
import com.vonage.client.verify.VerifyRequest
import com.vonage.client.verify.VerifyResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class VonageClientWrapper {

    @Value("\${vonage_auth.brand_name}")
    private lateinit var brandName: String

    @Value("\${vonage_auth.expiry}")
    private lateinit var expiry: String

    @Value("\${vonage_auth.api_key}")
    private lateinit var apiKey: String

    @Value("\${vonage_auth.api_secret}")
    private lateinit var apiSecret: String

    private val vonageClient: VonageClient by lazy {
        createVonageClient()
    }
    fun verify(verifyRequest: VerifyReq): VerifyResponse = vonageClient.verifyClient.verify(createVerifyRequest(verifyRequest))
    fun check(requestId: String, code: String): CheckResponse = vonageClient.verifyClient.check(requestId, code)

    private fun createVonageClient(): VonageClient {
        return VonageClient.builder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .build()
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

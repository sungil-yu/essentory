package com.essentory.controller.support

import com.vonage.client.verify.VerifyStatus

data class VonageExceptionDto(
        val requestId: String? = "",
        val status: VerifyStatus,
        val errorText: String? = "",
        val network: String? = ""
)

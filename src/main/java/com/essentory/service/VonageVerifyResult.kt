package com.essentory.service

data class VonageVerifyResult(
        val requestId: String,
        val success: Boolean,
        val errorText: String,
) {

}

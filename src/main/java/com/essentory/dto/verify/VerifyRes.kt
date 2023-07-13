package com.essentory.dto.verify

data class VerifyRes(
        val requestId: String?,
        val success: Boolean,
        val errorText: String?
)

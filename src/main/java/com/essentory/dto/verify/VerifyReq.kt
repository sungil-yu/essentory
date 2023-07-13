package com.essentory.dto.verify

import jakarta.validation.constraints.NotBlank

data class VerifyReq(
        val countryCode: @NotBlank String,
        val recipientNumber: @NotBlank String
) {
    fun phoneNumber(): String {
        return countryCode + recipientNumber
    }
}

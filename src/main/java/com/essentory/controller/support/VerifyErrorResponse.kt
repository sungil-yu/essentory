package com.essentory.controller.support

import org.springframework.http.HttpStatus

data class VerifyErrorResponse(
        val status: HttpStatus,
        val message: String,
        val requestId: String? = ""
){
    constructor(status: HttpStatus, message: String) : this(status, message, "")
}

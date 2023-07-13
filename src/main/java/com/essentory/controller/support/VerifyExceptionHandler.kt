package com.essentory.controller.support

import com.essentory.exception.BlackListNumberException
import com.essentory.exception.CriticalStatusCodeException
import com.essentory.exception.RestrictedCountryException
import com.vonage.client.verify.VerifyStatus
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

// hide server error
@ResponseStatus(HttpStatus.BAD_REQUEST)
@RestControllerAdvice
class VerifyExceptionHandler {

    val log = LoggerFactory.getLogger(this.javaClass)!!

    @ExceptionHandler(BlackListNumberException::class)
    fun handleBlackListNumberException(e: Exception, @RequestAttribute("requestId") requestId: String?): ResponseEntity<VerifyErrorResponse> {
        log.info("[vonage-blacklist] blacklist number,  request id : {}", requestId)
        log.info(e.message, e)
        return ResponseEntity.badRequest().body(VerifyErrorResponse(HttpStatus.BAD_REQUEST, e.message ?: "", requestId ?: ""))
    }

    //https://api.support.vonage.com/hc/en-us/articles/360018406532-Verify-On-demand-Service-to-High-Risk-Countries
    @ExceptionHandler(RestrictedCountryException::class)
    fun handleRestrictedCountryException(e: Exception, @RequestAttribute("requestId") requestId: String?): ResponseEntity<VerifyErrorResponse> {
        log.info("[vonage-restricted] unsupported network (Request restricted country), request id : {}", requestId)
        log.info(e.message, e)
        return ResponseEntity.badRequest().body(VerifyErrorResponse(HttpStatus.BAD_REQUEST, e.message ?: "", requestId ?: ""))
    }

    //https://developer.vonage.com/en/api/verify#verifyCheck-responses
    @ExceptionHandler(CriticalStatusCodeException::class)
    fun handleCriticalStatusException(e: Exception,
            @RequestAttribute("requestId") requestId: String?,
            @RequestAttribute("status") status: VerifyStatus,
            @RequestAttribute("errorText") errorText: String): ResponseEntity<VerifyErrorResponse> {
        log.error("[vonage-critical] critical status code {}", status)
        log.error("[vonage-critical] request id : {} ", requestId)
        log.error("[vonage-critical] error text : {}", errorText)
        log.error(e.message, e)
        return ResponseEntity.badRequest().body(VerifyErrorResponse(HttpStatus.BAD_REQUEST, "Please try again later"))
    }

    @ExceptionHandler(CriticalStatusCodeException::class)
    fun handleUnprocessableVonageError(e: Exception): ResponseEntity<VerifyErrorResponse> {
        log.info("[vonage] network, response parsing error")
        log.error(e.message, e)
        return ResponseEntity.badRequest().body(VerifyErrorResponse(HttpStatus.BAD_REQUEST, "Please try again later"))
    }
}

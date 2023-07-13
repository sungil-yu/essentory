package com.essentory.controller.support

import com.essentory.exceptions.*
import com.vonage.client.VonageClientException
import com.vonage.client.VonageResponseParseException
import com.vonage.client.verify.VerifyStatus
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@ResponseStatus(HttpStatus.BAD_REQUEST)
@RestControllerAdvice
class VerifyExceptionHandler {

    private val log = LoggerFactory.getLogger(this.javaClass)!!

    @ExceptionHandler(BlackListNumberException::class)
    fun handleBlackListNumberException(e: VerifyException): ResponseEntity<VerifyErrorResponse> {
        val vonageExceptionDto = e.getVonageExceptionDto()
        log.info("[vonage-blacklist] blacklist number, request id : {}", vonageExceptionDto.requestId?.ifBlank {vonageExceptionDto.network})
        return ResponseEntity.badRequest().body(VerifyErrorResponse(HttpStatus.BAD_REQUEST, e.message ?: e.defaultMessage, vonageExceptionDto.requestId?.ifBlank {vonageExceptionDto.network}))
    }

    //https://api.support.vonage.com/hc/en-us/articles/360018406532-Verify-On-demand-Service-to-High-Risk-Countries
    @ExceptionHandler(RestrictedCountryException::class)
    fun handleRestrictedCountryException(e: VerifyException): ResponseEntity<VerifyErrorResponse> {
        val vonageExceptionDto = e.getVonageExceptionDto()
        log.info("[vonage-restricted] unsupported network (Request restricted country), request id : {}", vonageExceptionDto.requestId?.ifBlank {vonageExceptionDto.network})
        return ResponseEntity.badRequest().body(VerifyErrorResponse(HttpStatus.BAD_REQUEST, e.message ?: e.defaultMessage, vonageExceptionDto.requestId?.ifBlank {vonageExceptionDto.network}))
    }

    //https://developer.vonage.com/en/api/verify#verifyCheck-responses
    @ExceptionHandler(CriticalStatusCodeException::class)
    fun handleCriticalStatusException(e: VerifyException): ResponseEntity<VerifyErrorResponse> {
        val vonageExceptionDto = e.getVonageExceptionDto()
        log.error("[vonage-critical] critical status code {}", vonageExceptionDto.status)
        log.error("[vonage-critical] request id : {} ", vonageExceptionDto.requestId?.ifBlank {vonageExceptionDto.network})
        log.error("[vonage-critical] error text : {}", vonageExceptionDto.errorText)
        log.error(e.message, e)
        return ResponseEntity.badRequest().body(VerifyErrorResponse(HttpStatus.BAD_REQUEST, e.defaultMessage))
    }
    @ExceptionHandler(VerifyCodeMismatchException::class)
    fun handleVerifyCodeMismatchException(e: VerifyException): ResponseEntity<VerifyErrorResponse> {
        val vonageExceptionDto = e.getVonageExceptionDto()
        log.info("[vonage-codeMismatch] verify code mismatch,  request id : {} ", vonageExceptionDto.requestId?.ifBlank {vonageExceptionDto.network})
        return ResponseEntity.badRequest().body(VerifyErrorResponse(HttpStatus.BAD_REQUEST, e.message ?: e.defaultMessage, vonageExceptionDto.requestId?.ifBlank {vonageExceptionDto.network}))
    }
    @ExceptionHandler(RepeatedInvalidCodeException::class)
    fun handleRepeatedInvalidCodeException(e: VerifyException): ResponseEntity<VerifyErrorResponse> {
        val vonageExceptionDto = e.getVonageExceptionDto()
        log.info("[vonage-wrong_code_throttled] The wrong code was provided too many times,  request id : {} ", vonageExceptionDto.requestId?.ifBlank {vonageExceptionDto.network})
        return ResponseEntity.badRequest().body(VerifyErrorResponse(HttpStatus.BAD_REQUEST, e.message ?: e.defaultMessage, vonageExceptionDto.requestId?.ifBlank {vonageExceptionDto.network}))
    }

    @ExceptionHandler(VonageClientException::class)
    fun handleVonageClientException(e: RuntimeException): ResponseEntity<VerifyErrorResponse> {
        log.info("[vonage] if there was a problem with the Vonage request or response objects.")
        log.error(e.message, e)
        return ResponseEntity.badRequest().body(VerifyErrorResponse(HttpStatus.BAD_REQUEST, "Please try again later"))
    }

    @ExceptionHandler(VonageResponseParseException::class)
    fun handleResponseParseException(e: RuntimeException): ResponseEntity<VerifyErrorResponse> {
        log.info("[vonage] response parsing error")
        log.error(e.message, e.printStackTrace())
        return ResponseEntity.badRequest().body(VerifyErrorResponse(HttpStatus.BAD_REQUEST, "Please try again later"))
    }

}

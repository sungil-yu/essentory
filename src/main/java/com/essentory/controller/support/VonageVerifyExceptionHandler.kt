package com.essentory.controller.support

import com.essentory.exceptions.*
import com.vonage.client.VonageClientException
import com.vonage.client.VonageResponseParseException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class VonageVerifyExceptionHandler {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${vonage_auth.exception.default_message}")
    private lateinit var defaultMessage: String

    @ExceptionHandler(BlackListNumberException::class)
    fun handleBlackListNumberException(e: VonageVerificationException): ResponseEntity<ErrorDetails> {
        val vonageExceptionDto = e.getVonageExceptionDto()
        log.info("[vonage-blacklist] blacklist number, request id : {}", vonageExceptionDto.requestId?.ifBlank {vonageExceptionDto.network})
        return ResponseEntity.badRequest().body(ErrorDetails(HttpStatus.BAD_REQUEST, e.message ?: defaultMessage, vonageExceptionDto.requestId?.ifBlank {vonageExceptionDto.network}))
    }

    //https://api.support.vonage.com/hc/en-us/articles/360018406532-Verify-On-demand-Service-to-High-Risk-Countries
    @ExceptionHandler(RestrictedCountryException::class)
    fun handleRestrictedCountryException(e: VonageVerificationException): ResponseEntity<ErrorDetails> {
        val vonageExceptionDto = e.getVonageExceptionDto()
        log.info("[vonage-restricted] unsupported network (Request restricted country), request id : {}", vonageExceptionDto.requestId?.ifBlank {vonageExceptionDto.network})
        return ResponseEntity.badRequest().body(ErrorDetails(HttpStatus.BAD_REQUEST, e.message ?: defaultMessage, vonageExceptionDto.requestId?.ifBlank {vonageExceptionDto.network}))
    }

    //https://developer.vonage.com/en/api/verify#verifyCheck-responses
    @ExceptionHandler(CriticalStatusCodeException::class)
    fun handleCriticalStatusException(e: VonageVerificationException): ResponseEntity<ErrorDetails> {
        val vonageExceptionDto = e.getVonageExceptionDto()
        log.error("[vonage-critical] critical status code {}", vonageExceptionDto.status)
        log.error("[vonage-critical] request id : {} ", vonageExceptionDto.requestId?.ifBlank {vonageExceptionDto.network})
        log.error("[vonage-critical] error text : {}", vonageExceptionDto.errorText)
        log.error(e.message, e)
        return ResponseEntity.badRequest().body(ErrorDetails(HttpStatus.BAD_REQUEST, defaultMessage))
    }
    @ExceptionHandler(VonageVerificationCodeMismatchException::class)
    fun handleVerifyCodeMismatchException(e: VonageVerificationException): ResponseEntity<ErrorDetails> {
        val vonageExceptionDto = e.getVonageExceptionDto()
        log.info("[vonage-codeMismatch] verify code mismatch,  request id : {} ", vonageExceptionDto.requestId?.ifBlank {vonageExceptionDto.network})
        return ResponseEntity.badRequest().body(ErrorDetails(HttpStatus.BAD_REQUEST, e.message ?: defaultMessage, vonageExceptionDto.requestId?.ifBlank {vonageExceptionDto.network}))
    }
    @ExceptionHandler(RepeatedInvalidCodeException::class)
    fun handleRepeatedInvalidCodeException(e: VonageVerificationException): ResponseEntity<ErrorDetails> {
        val vonageExceptionDto = e.getVonageExceptionDto()
        log.info("[vonage-wrong_code_throttled] The wrong code was provided too many times,  request id : {} ", vonageExceptionDto.requestId?.ifBlank {vonageExceptionDto.network})
        return ResponseEntity.badRequest().body(ErrorDetails(HttpStatus.BAD_REQUEST, e.message ?: defaultMessage, vonageExceptionDto.requestId?.ifBlank {vonageExceptionDto.network}))
    }

    @ExceptionHandler(VonageClientException::class, VonageResponseParseException::class)
    fun handleVonageException(e: RuntimeException): ResponseEntity<ErrorDetails> {
        log.info("[vonage] if there was a problem with the Vonage request or response objects.")
        log.error(e.message, e)
        val errorDetails = ErrorDetails(HttpStatus.BAD_REQUEST, defaultMessage, null)
        return ResponseEntity.badRequest().body(errorDetails)
    }

}

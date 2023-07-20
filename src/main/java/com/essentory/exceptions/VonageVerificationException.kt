package com.essentory.exceptions

import com.essentory.controller.support.VonageExceptionDto

abstract class VonageVerificationException(message: String?, private val vonageExceptionDto: VonageExceptionDto) : RuntimeException(message) {
    fun getVonageExceptionDto(): VonageExceptionDto {
        return vonageExceptionDto
    }
}

class InvalidParamsException(message: String?, vonageExceptionDto: VonageExceptionDto) : VonageVerificationException(message, vonageExceptionDto)
class BlackListNumberException(message: String?, vonageExceptionDto: VonageExceptionDto) : VonageVerificationException(message, vonageExceptionDto)
class CriticalStatusCodeException(message: String?, vonageExceptionDto: VonageExceptionDto) : VonageVerificationException(message, vonageExceptionDto)
class RestrictedCountryException(message: String?, vonageExceptionDto: VonageExceptionDto) : VonageVerificationException(message, vonageExceptionDto)
class MissingParamsException(message: String?, vonageExceptionDto: VonageExceptionDto) : VonageVerificationException(message, vonageExceptionDto)
class VonageVerificationCodeMismatchException(message: String?, vonageExceptionDto: VonageExceptionDto) : VonageVerificationException(message, vonageExceptionDto)
class RepeatedInvalidCodeException(message: String?, vonageExceptionDto: VonageExceptionDto) : VonageVerificationException(message, vonageExceptionDto)
class VonageException(message: String?, cause: Throwable?) : RuntimeException(message, cause)

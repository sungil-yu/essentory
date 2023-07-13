package com.essentory.exceptions

import com.essentory.controller.support.VonageExceptionDto

abstract class VerifyException : RuntimeException {

    private var vonageExceptionDto: VonageExceptionDto

    val defaultMessage: String
        get() {
            return "Please try again later"
        }
    constructor(message: String?, vonageExceptionDto: VonageExceptionDto) : super(message) {
        this.vonageExceptionDto = vonageExceptionDto
    }

    constructor(message: String?, cause: Throwable?, vonageExceptionDto: VonageExceptionDto) : super(message, cause) {
        this.vonageExceptionDto = vonageExceptionDto
    }
    fun getVonageExceptionDto(): VonageExceptionDto {
        return vonageExceptionDto
    }
}

class BlackListNumberException : VerifyException {

    constructor(message: String?, vonageExceptionDto: VonageExceptionDto) : super(message, vonageExceptionDto)
    constructor(message: String?, cause: Throwable?, vonageExceptionDto: VonageExceptionDto) : super(message, cause, vonageExceptionDto)
}

class CriticalStatusCodeException : VerifyException {

    constructor(message: String?, vonageExceptionDto: VonageExceptionDto) : super(message, vonageExceptionDto)
    constructor(message: String?, cause: Throwable?, vonageExceptionDto: VonageExceptionDto) : super(message, cause, vonageExceptionDto)
}

class RestrictedCountryException : VerifyException {

    constructor(message: String?, vonageExceptionDto: VonageExceptionDto) : super(message, vonageExceptionDto)
    constructor(message: String?, cause: Throwable?, vonageExceptionDto: VonageExceptionDto) : super(message, cause, vonageExceptionDto)
}

class VerifyCodeMismatchException : VerifyException {
    constructor(message: String?, vonageExceptionDto: VonageExceptionDto) : super(message, vonageExceptionDto)
    constructor(message: String?, cause: Throwable?, vonageExceptionDto: VonageExceptionDto) : super(message, cause, vonageExceptionDto)
}


class RepeatedInvalidCodeException : VerifyException {
    constructor(message: String?, vonageExceptionDto: VonageExceptionDto) : super(message, vonageExceptionDto)
    constructor(message: String?, cause: Throwable?, vonageExceptionDto: VonageExceptionDto) : super(message, cause, vonageExceptionDto)
}

class VonageException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

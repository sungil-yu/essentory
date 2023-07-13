package com.essentory.exceptions

import com.essentory.controller.support.VonageExceptionDto

abstract class VonageVerificationException : RuntimeException {

    private val vonageExceptionDto: VonageExceptionDto

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

class BlackListNumberException : VonageVerificationException {
    constructor(message: String?, vonageExceptionDto: VonageExceptionDto) : super(message, vonageExceptionDto)
    constructor(message: String?, cause: Throwable?, vonageExceptionDto: VonageExceptionDto) : super(message, cause, vonageExceptionDto)
}

class CriticalStatusCodeException : VonageVerificationException {
    constructor(message: String?, vonageExceptionDto: VonageExceptionDto) : super(message, vonageExceptionDto)
    constructor(message: String?, cause: Throwable?, vonageExceptionDto: VonageExceptionDto) : super(message, cause, vonageExceptionDto)
}

class RestrictedCountryException : VonageVerificationException {
    constructor(message: String?, vonageExceptionDto: VonageExceptionDto) : super(message, vonageExceptionDto)
    constructor(message: String?, cause: Throwable?, vonageExceptionDto: VonageExceptionDto) : super(message, cause, vonageExceptionDto)
}

class VonageVerificationCodeMismatchException : VonageVerificationException {
    constructor(message: String?, vonageExceptionDto: VonageExceptionDto) : super(message, vonageExceptionDto)
    constructor(message: String?, cause: Throwable?, vonageExceptionDto: VonageExceptionDto) : super(message, cause, vonageExceptionDto)
}


class RepeatedInvalidCodeException : VonageVerificationException {
    constructor(message: String?, vonageExceptionDto: VonageExceptionDto) : super(message, vonageExceptionDto)
    constructor(message: String?, cause: Throwable?, vonageExceptionDto: VonageExceptionDto) : super(message, cause, vonageExceptionDto)
}

class VonageException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

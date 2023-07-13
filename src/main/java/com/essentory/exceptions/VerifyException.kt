package com.essentory.exceptions

import com.vonage.client.verify.VerifyStatus

abstract class VerifyException : RuntimeException {
    private val requestId: String?

    constructor(message: String?, requestId: String? = null) : super(message) {
        this.requestId = requestId
    }

    constructor(message: String?, cause: Throwable?, requestId: String? = null) : super(message, cause) {
        this.requestId = requestId
    }
}

class BlackListNumberException : VerifyException {
    constructor(message: String?, requestId: String? = null) : super(message, requestId)
    constructor(message: String?, cause: Throwable?, requestId: String? = null) : super(message, cause, requestId)
}

class CriticalStatusCodeException : VerifyException {
    constructor(message: String?, requestId: String? = null) : super(message, requestId)
    constructor(message: String?, requestId: String? = null, status: VerifyStatus, errorText: String) : super(message, requestId)
    constructor(message: String?, cause: Throwable?, requestId: String? = null) : super(message, cause, requestId)
}

class RestrictedCountryException : VerifyException {
    constructor(message: String?, requestId: String? = null) : super(message, requestId)
    constructor(message: String?, cause: Throwable?, requestId: String? = null) : super(message, cause, requestId)
}

class VonageException : VerifyException {
    constructor(message: String?, requestId: String? = null) : super(message, requestId)
    constructor(message: String?, cause: Throwable?, requestId: String? = null) : super(message, cause, requestId)
}

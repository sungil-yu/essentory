package com.essentory.dto.verify

class VerifyDto<T> private constructor(
        val response: T
) {
    companion object {
        fun <T> of(response: T): VerifyDto<T> {
            return VerifyDto(response)
        }
    }
}

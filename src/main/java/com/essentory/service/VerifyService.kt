package com.essentory.service

import com.essentory.dto.verify.VerifyDto
import com.essentory.dto.verify.VerifyReq
import com.essentory.dto.verify.VerifyRes

interface VerifyService {
    fun verifyPhone(verifyReq: VerifyReq): VerifyRes
    fun verifyCode(requestId: String, code: String): VerifyRes
}

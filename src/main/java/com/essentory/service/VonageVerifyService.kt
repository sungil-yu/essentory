package com.essentory.service

import com.essentory.dto.verify.VerifyReq
import com.essentory.dto.verify.VerifyRes
import org.springframework.stereotype.Service



@Service
class VonageVerifyService(
        private val vonageVerifyAdapter: VonageVerifyAdapter,
) : VerifyService {
    override fun verifyPhone(verifyReq: VerifyReq): VerifyRes = vonageVerifyAdapter.requestVerifyCode(verifyReq)
    override fun verifyCode(requestId: String, code: String): VerifyRes = vonageVerifyAdapter.verifyCode(requestId, code)
}

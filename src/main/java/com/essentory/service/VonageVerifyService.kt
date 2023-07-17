package com.essentory.service

import com.essentory.dto.verify.VerifyReq
import com.essentory.dto.verify.VerifyRes
import org.springframework.stereotype.Service



@Service
class VonageVerifyService(
    private val vonageClientWrapper: VonageClientWrapper,
) : VerifyService {


    override fun verifyPhone(verifyReq: VerifyReq): VerifyRes {

        val result = vonageClientWrapper.verify(verifyReq)

        return VerifyRes(
                requestId = result.requestId,
                success = result.success,
                errorText = result.errorText
        )
    }

    override fun verifyCode(requestId: String, code: String): VerifyRes {

        val result = vonageClientWrapper.check(requestId, code)

        return VerifyRes(
                requestId = result.requestId,
                success = result.success,
                errorText = result.errorText
        )
    }
}

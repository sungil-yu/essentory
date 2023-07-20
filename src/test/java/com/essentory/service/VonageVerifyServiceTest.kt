package com.essentory.service

import com.essentory.dto.verify.VerifyReq
import com.essentory.util.BaseIntegrationTest
import com.vonage.client.verify.VerifyResponse
import com.vonage.client.verify.VerifyStatus
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.mock.mockito.MockBean

class VonageVerifyServiceTest : BaseIntegrationTest() {

    @Autowired
    lateinit var verifyService: VonageVerifyService

    @MockBean
    lateinit var vonageClientWrapper: VonageClientWrapper

    @DisplayName("국가 코드와 사용자 번호를 통해 휴대폰 인증 코드 발송에 성공한다.")
    @Test
    fun verifyPhone() {
        val verifyReq = VerifyReq("82", "1012345678")
        given(vonageClientWrapper.requestVerifyCode(verifyReq))
                .willReturn(VerifyResponse(VerifyStatus.OK))

        val result = verifyService.verifyPhone(verifyReq)

        assertThat(result.success).isTrue
    }



}

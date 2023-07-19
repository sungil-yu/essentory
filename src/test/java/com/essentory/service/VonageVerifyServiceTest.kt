package com.essentory.service

import com.essentory.dto.verify.VerifyReq
import com.essentory.dto.verify.VerifyRes
import com.vonage.client.VonageClient
import com.vonage.client.verify.VerifyRequest
import com.vonage.client.verify.VerifyResponse
import com.vonage.client.verify.VerifyStatus
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito
import org.mockito.BDDMockito.*
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
class VonageVerifyServiceTest {

    @Autowired
    lateinit var verifyService: VonageVerifyService

    @MockBean
    lateinit var vonageClientWrapper: VonageClientWrapper

    @DisplayName("국가 코드와 사용자 번호를 통해 휴대폰 인증 코드 발송에 성공한다.")
    @Test
    fun verifyPhone(@Value("\${vonage_auth.brand_name}") brandName: String,
                    @Value("\${vonage_auth.expiry}") expiry: String = "180") {
        val verifyReq = VerifyReq("82", "01012345678")
        given(vonageClientWrapper.verify(verifyReq))
                .willReturn(VerifyResponse(VerifyStatus.OK))

        val result = verifyService.verifyPhone(verifyReq)

        assertThat(result.success).isTrue
    }

}

package com.essentory.service

import com.essentory.dto.verify.VerifyReq
import com.essentory.exceptions.*
import com.essentory.util.BaseIntegrationTest
import com.vonage.client.VonageClientException
import com.vonage.client.VonageResponseParseException
import com.vonage.client.verify.CheckResponse
import com.vonage.client.verify.VerifyResponse
import com.vonage.client.verify.VerifyStatus
import org.assertj.core.api.Assertions.*
import org.hibernate.annotations.Check
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean

class VonageVerifyServiceTest : BaseIntegrationTest() {

    @Autowired
    lateinit var verifyService: VonageVerifyService

    @MockBean
    lateinit var vonageClientWrapper: VonageClientWrapper

    @DisplayName("국가 코드와 사용자 번호를 통해 휴대폰 인증 코드 발송에 성공한다.")
    @Test
    fun verifyPhone() {
        val verifyReq = VerifyReq("82", "01012345678")
        given(vonageClientWrapper.requestVerifyCode(verifyReq))
                .willReturn(VerifyResponse(VerifyStatus.OK))

        val result = verifyService.requestVerifyCode(verifyReq)

        assertThat(result.success).isTrue
    }

    @DisplayName("유효하지 않은 국가 코드와 사용자 번호를 통해 휴대폰 인증 코드 발송에 실패한다.")
    @Test
    fun requestVerifyCodeWithInvalidCountryCode() {
        val verifyReq = VerifyReq("1234", "01012345678")

        given(vonageClientWrapper.requestVerifyCode(verifyReq))
                .willReturn(VerifyResponse(VerifyStatus.INVALID_PARAMS))

        val exception = assertThrows<InvalidParamsException> {
            verifyService.requestVerifyCode(verifyReq)
        }

        assertThat(exception.message).isEqualTo("invalid params")
    }

    @DisplayName("요청 번호가 Vonage 서버에 블랙리스트로 등록된 경우")
    @Test
    fun requestVerifyCodeWithBlackListNumber() {
        val verifyReq = VerifyReq("82", "01012345678")

        given(vonageClientWrapper.requestVerifyCode(verifyReq))
                .willReturn(VerifyResponse(VerifyStatus.NUMBER_BARRED))

        val exception = assertThrows<BlackListNumberException> {
            verifyService.requestVerifyCode(verifyReq)
        }

        assertThat(exception.message).isEqualTo("blacklist number")
    }

    @DisplayName("제한된 국가에서 요청한 경우")
    @Test
    fun requestVerifyCodeWithUnsupportedNetwork() {

        val verifyReq = VerifyReq("82", "01012345678")

        given(vonageClientWrapper.requestVerifyCode(verifyReq))
                .willReturn(VerifyResponse(VerifyStatus.UNSUPPORTED_NETWORK))

        val exception = assertThrows<RestrictedCountryException> {
            verifyService.requestVerifyCode(verifyReq)
        }

        assertThat(exception.message).isEqualTo("bad request, restricted country")
    }

    @DisplayName("필수 요청 파라미터가 존재하지 않아 요청을 보낼 수 없는 경우")
    @Test
    fun requestVerifyCodeWithMissingParams() {
        val verifyReq = VerifyReq("82", "01012345678")

        given(vonageClientWrapper.requestVerifyCode(verifyReq))
                .willReturn(VerifyResponse(VerifyStatus.MISSING_PARAMS))

        val exception = assertThrows<MissingParamsException> {
            verifyService.requestVerifyCode(verifyReq)
        }

        exception.getVonageExceptionDto().let {
            assertThat(it.status).isEqualTo(VerifyStatus.MISSING_PARAMS)
            assertThat(exception.message).isEqualTo("missing params")
        }
    }

    @DisplayName("제공받은 apiKey, secretKey 유효하지 않은 경우")
    @Test
    fun requestVerifyCodeWithInvalidCredentials() {
        val verifyReq = VerifyReq("82", "01012345678")

        given(vonageClientWrapper.requestVerifyCode(verifyReq))
                .willReturn(VerifyResponse(VerifyStatus.INVALID_CREDENTIALS))

        val exception = assertThrows<CriticalStatusCodeException> {
            verifyService.requestVerifyCode(verifyReq)
        }

        exception.getVonageExceptionDto().let {
            assertThat(it.status).isEqualTo(VerifyStatus.INVALID_CREDENTIALS)
            assertThat(exception.message).isEqualTo("critical status code: ${it.status}")
        }
    }

    @DisplayName("Vonage 서버의 내부 클라우드 오류인 경우")
    @Test
    fun requestVerifyCodeWithInternalError() {
        val verifyReq = VerifyReq("82", "01012345678")

        given(vonageClientWrapper.requestVerifyCode(verifyReq))
                .willReturn(VerifyResponse(VerifyStatus.INTERNAL_ERROR))

        val exception = assertThrows<CriticalStatusCodeException> {
            verifyService.requestVerifyCode(verifyReq)
        }

        exception.getVonageExceptionDto().let {
            assertThat(it.status).isEqualTo(VerifyStatus.INTERNAL_ERROR)
            assertThat(exception.message).isEqualTo("critical status code: ${it.status}")
        }
    }

    @DisplayName("Vonage credit이 부족한 경우")
    @Test
    fun requestVerifyCodeWithQuotaExceeded() {
        val verifyReq = VerifyReq("82", "01012345678")

        given(vonageClientWrapper.requestVerifyCode(verifyReq))
                .willReturn(VerifyResponse(VerifyStatus.PARTNER_QUOTA_EXCEEDED))

        val exception = assertThrows<CriticalStatusCodeException> {
            verifyService.requestVerifyCode(verifyReq)
        }

        exception.getVonageExceptionDto().let {
            assertThat(it.status).isEqualTo(VerifyStatus.PARTNER_QUOTA_EXCEEDED)
            assertThat(exception.message).isEqualTo("critical status code: ${it.status}")
        }
    }


    @DisplayName("사용자의 인증 코드가 유효한 경우")
    @Test
    fun verifyCode() {
        val requestId = "requestId"
        val code = "1234"
        given(vonageClientWrapper.verifyCode(requestId, code))
                .willReturn(CheckResponse(VerifyStatus.OK))

        val result = verifyService.verifyCode(requestId, code)

        assertThat(result.success).isTrue
    }

    @DisplayName("사용자의 인증 코드가 유효하지 않은 경우")
    @Test
    fun verifyInvalidCode() {
        val requestId = "requestId"
        val code = "1234"
        val mockCheckResponse = mock(CheckResponse::class.java)
        given(mockCheckResponse.status).willReturn(VerifyStatus.INVALID_CODE)
        given(mockCheckResponse.requestId).willReturn(requestId)

        given(vonageClientWrapper.verifyCode(requestId, code))
                .willReturn(mockCheckResponse)

        val exception = assertThrows<VerifyCodeMismatchException> {
            verifyService.verifyCode(requestId, code)
        }

        exception.getVonageExceptionDto().let {
            assertThat(it.status).isEqualTo(VerifyStatus.INVALID_CODE)
            assertThat(it.requestId).isEqualTo(requestId)
        }

        assertThat(exception.message).isEqualTo("verify code mismatch")
    }

    @DisplayName("잘못된 인증 코드를 지속적으로 요청하는 경우")
    @Test
    fun verifyCodeWithTooManyRequests() {
        val requestId = "requestId"
        val code = "1234"

        val mockCheckResponse = mock(CheckResponse::class.java)
        given(mockCheckResponse.status).willReturn(VerifyStatus.WRONG_CODE_THROTTLED)
        given(mockCheckResponse.requestId).willReturn(requestId)

        given(vonageClientWrapper.verifyCode(requestId, code))
                .willReturn(mockCheckResponse)

        val exception = assertThrows<RepeatedInvalidCodeException> {
            verifyService.verifyCode(requestId, code)
        }

        exception.getVonageExceptionDto().let {
            assertThat(it.status).isEqualTo(VerifyStatus.WRONG_CODE_THROTTLED)
            assertThat(it.requestId).isEqualTo(requestId)
        }

        assertThat(exception.message).isEqualTo("The wrong code was provided too many times")
    }

    @DisplayName("Vonage 서버와 네트워크 연결이 안되는 경우")
    @Test
    fun requestVonageApiWithNetworkError() {
        val verifyReq = VerifyReq("82", "01012345678")

        given(vonageClientWrapper.requestVerifyCode(verifyReq))
                .willThrow(VonageClientException())

        val requestVerifyCodeException = assertThrows<VonageException> {
            verifyService.requestVerifyCode(verifyReq)
        }

        assertThat(requestVerifyCodeException.message).isEqualTo("vonage server network disconnected")


        val requestId = "requestId"
        val code = "1234"

        given(vonageClientWrapper.verifyCode(requestId, code))
                .willThrow(VonageClientException())

        val verifyCodeException = assertThrows<VonageException> {
            verifyService.verifyCode(requestId, code)
        }

        assertThat(verifyCodeException.message).isEqualTo("vonage server network disconnected")
    }

    @DisplayName("vonage Api 응답 결과를 파싱할 수 없는 경우")
    @Test
    fun requestVonageApiWithParsingError() {
        val verifyReq = VerifyReq("82", "01012345678")

        given(vonageClientWrapper.requestVerifyCode(verifyReq))
                .willThrow(VonageResponseParseException(null))

        val requestVerifyCodeException = assertThrows<VonageException> {
            verifyService.requestVerifyCode(verifyReq)
        }

        assertThat(requestVerifyCodeException.message).isEqualTo("response parsing error")

        val requestId = "requestId"
        val code = "1234"

        given(vonageClientWrapper.verifyCode(requestId, code))
                .willThrow(VonageResponseParseException(null))

        val verifyCodeException = assertThrows<VonageException> {
            verifyService.verifyCode(requestId, code)
        }

        assertThat(verifyCodeException.message).isEqualTo("response parsing error")
    }

}

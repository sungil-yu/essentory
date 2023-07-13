package com.essentory.controller

import com.essentory.dto.verify.VerifyDto
import com.essentory.dto.verify.VerifyReq
import com.essentory.dto.verify.VerifyRes
import com.essentory.service.VerifyService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/user")
class VerifyController (val verifyService: VerifyService){
    @PostMapping("/verify")
    fun usePhoneVerify(@RequestBody verifyReq: @Valid VerifyReq): ResponseEntity<VerifyDto<VerifyRes>> {
        val response = verifyService.verifyPhone(verifyReq)


        return ResponseEntity.ok(response)
    }

    @GetMapping("/verify/{requestId}")
    fun checkVerify(@RequestParam("code") code: String, @PathVariable("requestId") requestId: String): ResponseEntity<VerifyDto<Boolean>> {
        val checkResult = verifyService.verifyCode(requestId, code)
        return ResponseEntity.ok(VerifyDto.of(checkResult))
    }


}

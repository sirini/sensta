package me.domain.model.auth

// 회원가입 시 인증코드 전송하고 받는 결과 엔티티
data class TsboardVerifyCode(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: Boolean
)
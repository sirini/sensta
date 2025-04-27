package me.domain.model.auth

// 회원가입 시 인증코드 전송용 파라미터
data class TsboardVerifyCodeParam(
    val target: Int,
    val code: String,
    val email: String,
    val password: String,
    val name: String
)
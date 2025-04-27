package me.domain.model.auth

// 회원가입 시 응답 엔티티
data class TsboardSignup(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: TsboardSignupResult
)

// 회원가입 시 결과 엔티티
data class TsboardSignupResult(
    val sendmail: Boolean,
    val target: Int
)
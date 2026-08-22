package me.domain.model.auth

// 회원가입 시 응답 엔티티
data class NuboSignup(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: NuboSignupResult
)

// 회원가입 시 결과 엔티티
data class NuboSignupResult(
    val target: Int,
    val requiresVerification: Boolean,
    val completed: Boolean
)

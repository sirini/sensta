package me.domain.model.auth

// 리프레시 토큰으로 액세스 토큰 받기 엔티티
data class TsboardUpdateAccessToken(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: String? = null
)
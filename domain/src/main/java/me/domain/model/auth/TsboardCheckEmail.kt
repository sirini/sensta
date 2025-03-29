package me.domain.model.auth

// 이메일 주소 등록 여부 엔티티
data class TsboardCheckEmail(
    val success: Boolean,
    val error: String,
    val code: Int
)
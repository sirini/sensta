package me.domain.model.auth

// 사용자의 정보 업데이트 요청 응답 엔티티
data class TsboardUpdateUserInfo(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: String?
)
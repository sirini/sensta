package me.domain.model.user

import java.time.LocalDateTime

// 다른 사용자의 기본 정보 요청에 대한 응답 엔티티
data class TsboardOtherUserInfo(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: TsboardOtherUserInfoResult
)

// 다른 사용자의 기본 정보 엔티티
data class TsboardOtherUserInfoResult(
    val uid: Int,
    val name: String,
    val profile: String,
    val level: Int,
    val signature: String,
    val signup: LocalDateTime,
    val signin: LocalDateTime,
    val admin: Boolean,
    val blocked: Boolean
)
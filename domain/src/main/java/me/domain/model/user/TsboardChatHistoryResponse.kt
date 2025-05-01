package me.domain.model.user

import java.time.LocalDateTime

// 상대방과의 대화 기록 내용 응답 엔티티
data class TsboardChatHistoryResponse(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: List<TsboardChatHistory>
)

// 상대방과의 대화 기록들 엔티티
data class TsboardChatHistory(
    val uid: Int,
    val userUid: Int,
    val message: String,
    val timestamp: LocalDateTime
)
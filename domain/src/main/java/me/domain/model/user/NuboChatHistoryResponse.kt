package me.domain.model.user

import java.time.LocalDateTime

// 상대방과의 대화 기록 내용 응답 엔티티
data class NuboChatHistoryResponse(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: List<NuboChatHistory>
)

// 상대방과의 대화 기록들 엔티티
data class NuboChatHistory(
    val uid: Int,
    val userUid: Int,
    val message: String,
    val timestamp: LocalDateTime,
    val readAt: Long = 0
)

// 대화 읽음 처리 결과 엔티티
data class NuboChatReadResult(
    val throughUid: Int,
    val readAt: Long,
    val updatedCount: Long
)

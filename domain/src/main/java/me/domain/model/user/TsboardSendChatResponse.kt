package me.domain.model.user

// 상대에게 메시지 보내고 받은 응답 엔티티
data class TsboardSendChatResponse(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: Int
)
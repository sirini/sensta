package me.data.remote.dto.user

import kotlinx.serialization.Serializable
import me.domain.model.user.TsboardSendChatResponse

// 상대에게 메시지 보내고 받은 JSON 응답
@Serializable
data class SendChatResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: Int
)

// 응답을 엔티티로 변환하는 매퍼
fun SendChatResponseDto.toEntity(): TsboardSendChatResponse = TsboardSendChatResponse(
    success = success,
    error = error,
    code = code,
    result = result
)
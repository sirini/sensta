package me.data.remote.dto.user

import kotlinx.serialization.Serializable
import me.domain.model.user.NuboSendChatResponse

// 상대에게 메시지 보내고 받은 JSON 응답
@Serializable
data class SendChatResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: Int = 0
)

// 상대방에게 메시지를 보내는 JSON 요청
@Serializable
data class SendChatRequestDto(
    val targetUserUid: Int,
    val message: String
)

// 응답을 엔티티로 변환하는 매퍼
fun SendChatResponseDto.toEntity() = NuboSendChatResponse(
    success = success,
    error = error,
    code = code,
    result = result
)

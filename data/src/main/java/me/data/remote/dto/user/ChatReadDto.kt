package me.data.remote.dto.user

import kotlinx.serialization.Serializable
import me.domain.model.user.NuboChatReadResult

// 상대방이 보낸 메시지를 어디까지 확인했는지 서버에 전달한다.
@Serializable
data class ChatReadRequestDto(
    val targetUserUid: Int,
    val throughUid: Int
)

@Serializable
data class ChatReadResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: ChatReadResultDto? = null
)

@Serializable
data class ChatReadResultDto(
    val throughUid: Int,
    val readAt: Long,
    val updatedCount: Long
)

fun ChatReadResultDto.toEntity() = NuboChatReadResult(
    throughUid = throughUid,
    readAt = readAt,
    updatedCount = updatedCount
)

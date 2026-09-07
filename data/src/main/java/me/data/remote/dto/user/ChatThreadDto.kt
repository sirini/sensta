package me.data.remote.dto.user

import kotlinx.serialization.Serializable
import me.domain.model.user.NuboChatThread
import java.time.Instant
import java.time.ZoneOffset

@Serializable
data class ChatThreadListResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: List<ChatThreadDto> = emptyList()
)

@Serializable
data class ChatThreadDto(
    val sender: ChatThreadSenderDto,
    val uid: Int,
    val message: String,
    val timestamp: Long
)

@Serializable
data class ChatThreadSenderDto(
    val uid: Int,
    val name: String,
    val profile: String
)

fun ChatThreadDto.toEntity() = NuboChatThread(
    senderUid = sender.uid,
    senderName = sender.name,
    senderProfile = sender.profile,
    latestMessageUid = uid,
    latestMessage = message,
    timestamp = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(9)).toLocalDateTime()
)

fun ChatThreadListResponseDto.toEntity(): List<NuboChatThread> = result
    .map(ChatThreadDto::toEntity)
    .sortedWith(compareByDescending<NuboChatThread> { it.timestamp }.thenByDescending { it.latestMessageUid })

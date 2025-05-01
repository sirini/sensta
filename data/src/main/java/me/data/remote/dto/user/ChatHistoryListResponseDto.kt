package me.data.remote.dto.user

import kotlinx.serialization.Serializable
import me.domain.model.user.TsboardChatHistory
import me.domain.model.user.TsboardChatHistoryResponse
import java.time.Instant
import java.time.ZoneOffset

// 상대방과의 최근 대화 기록 가져오기 JSON 응답
@Serializable
data class ChatHistoryListResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: List<ChatHistoryResultDto>
)

// 상대방과의 최근 대화 기록 응답 결과
@Serializable
data class ChatHistoryResultDto(
    val uid: Int,
    val userUid: Int,
    val message: String,
    val timestamp: Long
)

// 상대방과의 최근 대화 기록 응답을 엔티티로 변환하는 매퍼
fun ChatHistoryListResponseDto.toEntity(): TsboardChatHistoryResponse = TsboardChatHistoryResponse(
    success = success,
    error = error,
    code = code,
    result = result.map { it.toEntity() }
)

// 상대방과의 대화 내용을 엔티티로 변환하는 매퍼
fun ChatHistoryResultDto.toEntity(): TsboardChatHistory = TsboardChatHistory(
    uid = uid,
    userUid = userUid,
    message = message,
    timestamp = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(9)).toLocalDateTime()
)
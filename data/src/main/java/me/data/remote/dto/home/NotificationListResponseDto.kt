package me.data.remote.dto.home

import kotlinx.serialization.Serializable
import me.data.remote.dto.common.WriterSimpleDto
import me.data.remote.dto.common.toEntity
import me.domain.model.home.TsboardNotification
import me.domain.model.home.TsboardNotificationResponse
import java.time.Instant
import java.time.ZoneOffset

// 알림 내역 가져오기에 대한 JSON 응답
@Serializable
data class NotificationListResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: List<NotificationResultDto>?
)

// 알림 리스트 JSON 응답
@Serializable
data class NotificationResultDto(
    val uid: Int,
    val fromUser: WriterSimpleDto,
    val type: Int,
    val id: String,
    val boardType: Int,
    val postUid: Int,
    val checked: Boolean,
    val timestamp: Long
)

// 알림 내역 가져오기에 대한 응답을 엔티티로 변환하는 매퍼
fun NotificationListResponseDto.toEntity() = TsboardNotificationResponse(
    success = success,
    error = error,
    code = code,
    result = result?.map { it.toEntity() } ?: emptyList()
)

// 알림 항목에 대해 엔티티로 변환하는 매퍼
fun NotificationResultDto.toEntity() = TsboardNotification(
    uid = uid,
    fromUser = fromUser.toEntity(),
    type = type,
    id = id,
    boardType = boardType,
    postUid = postUid,
    checked = checked,
    timestamp = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(9)).toLocalDateTime()
)
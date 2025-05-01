package me.domain.model.home

import me.domain.model.common.TsboardWriter
import java.time.LocalDateTime

// 알림 내역 응답 엔티티
data class TsboardNotification(
    val uid: Int,
    val fromUser: TsboardWriter,
    val type: Int,
    val id: String,
    val boardType: Int,
    val postUid: Int,
    val checked: Boolean,
    val timestamp: LocalDateTime
)

// 알림 타입 변환
object NotificationType {
    const val NOTI_LIKE_POST = 0
    const val NOTI_LIKE_COMMENT = 1
    const val NOTI_LEAVE_COMMENT = 2
    const val NOTI_REPLY_COMMENT = 3
    const val NOTI_CHAT_MESSAGE = 4
}
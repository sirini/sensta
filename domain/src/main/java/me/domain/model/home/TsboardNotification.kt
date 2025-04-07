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
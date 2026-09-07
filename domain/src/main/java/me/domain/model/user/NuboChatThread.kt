package me.domain.model.user

import java.time.LocalDateTime

data class NuboChatThread(
    val senderUid: Int,
    val senderName: String,
    val senderProfile: String,
    val latestMessageUid: Int,
    val latestMessage: String,
    val timestamp: LocalDateTime
)

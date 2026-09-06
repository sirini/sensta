package me.sensta.ui.screen.user

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val chatTimestampFormatter =
    DateTimeFormatter.ofPattern("M월 d일 a h:mm", Locale.KOREAN)

internal fun formatChatTimestamp(timestamp: LocalDateTime): String =
    timestamp.format(chatTimestampFormatter)

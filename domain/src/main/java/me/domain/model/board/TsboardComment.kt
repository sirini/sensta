package me.domain.model.board

import me.domain.model.common.TsboardWriter
import java.time.LocalDateTime

// 댓글 엔티티
data class TsboardComment(
    val uid: Int,
    val replyUid: Int,
    val postUid: Int,
    val writer: TsboardWriter,
    val like: Int,
    val liked: Boolean,
    val submitted: LocalDateTime,
    val status: Int,
    val content: String
)
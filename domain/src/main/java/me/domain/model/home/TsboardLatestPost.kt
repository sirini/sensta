package me.domain.model.home

import me.domain.model.board.TsboardCategory
import me.domain.model.common.TsboardWriter
import java.time.LocalDateTime

// 최근글 응답 엔티티
data class TsboardLatestPost(
    val uid: Int,
    val title: String,
    val content: String,
    val submitted: LocalDateTime,
    val hit: Int,
    val status: Int,
    val category: TsboardCategory,
    val cover: String,
    val comment: Int,
    val like: Int,
    val liked: Boolean,
    val writer: TsboardWriter,
    val id: String,
    val type: Int,
    val useCategory: Boolean
)
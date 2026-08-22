package me.domain.model.home

import me.domain.model.board.NuboCategory
import me.domain.model.common.NuboWriter
import java.time.LocalDateTime

// 최근글 응답 엔티티
data class NuboLatestPost(
    val uid: Int,
    val title: String,
    val content: String,
    val submitted: LocalDateTime,
    val hit: Int,
    val status: Int,
    val category: NuboCategory,
    val cover: String,
    val comment: Int,
    val like: Int,
    val liked: Boolean,
    val writer: NuboWriter,
    val id: String,
    val type: Int,
    val useCategory: Boolean
)

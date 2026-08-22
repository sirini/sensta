package me.domain.model.board

import me.domain.model.common.NuboWriter
import java.time.LocalDateTime

// 게시글 엔티티
data class NuboPost(
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
    val writer: NuboWriter
)

// 게시글 카테고리 엔티티
data class NuboCategory(
    val uid: Int,
    val name: String
)

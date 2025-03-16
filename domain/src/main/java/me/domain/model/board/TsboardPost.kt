package me.domain.model.board

import java.time.LocalDateTime

// 게시글 엔티티
data class TsboardPost(
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
    val writer: TsboardWriter
)

// 게시글 카테고리 엔티티
data class TsboardCategory(
    val uid: Int,
    val name: String
)

// 게시글 작성자 엔티티
data class TsboardWriter(
    val uid: Int,
    val name: String,
    val profile: String,
    val signature: String
)
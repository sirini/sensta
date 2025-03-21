package me.domain.model.common

// 게시글 작성자 엔티티
data class TsboardWriter(
    val uid: Int,
    val name: String,
    val profile: String,
    val signature: String
)
package me.domain.model.board

data class NuboModifyCommentParam(
    val boardUid: Int,
    val postUid: Int,
    val commentUid: Int,
    val content: String,
    val token: String
)

package me.domain.model.board

data class NuboModifyPostParam(
    val boardUid: Int,
    val postUid: Int,
    val categoryUid: Int,
    val isNotice: Boolean,
    val isSecret: Boolean,
    val title: String,
    val content: String,
    val tags: List<String>,
    val token: String
)

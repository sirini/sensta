package me.domain.model.board

// 댓글 작성에 필요한 파라미터 정의
data class NuboWriteCommentParam(
    val boardUid: Int,
    val postUid: Int,
    val content: String,
    val token: String,
    val replyTargetUid: Int? = null
)

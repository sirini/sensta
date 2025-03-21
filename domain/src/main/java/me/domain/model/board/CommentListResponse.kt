package me.domain.model.board

// 댓글 목록 가져오기 엔티티
data class CommentListResponse(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: CommentListResult
)

// 댓글 목록 Result 엔티티
data class CommentListResult(
    val boardUid: Int,
    val sinceUid: Int,
    val totalCommentCount: Int,
    val comments: List<TsboardComment>
)
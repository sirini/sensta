package me.domain.model.board

// 댓글 목록 가져오기 엔티티
data class TsboardCommentListResponse(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: TsboardCommentListResult
)

// 댓글 목록 Result 엔티티
data class TsboardCommentListResult(
    val boardUid: Int,
    val sinceUid: Int,
    val totalCommentCount: Int,
    val comments: List<TsboardComment>
)
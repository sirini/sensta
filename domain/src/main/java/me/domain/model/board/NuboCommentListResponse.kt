package me.domain.model.board

// 댓글 목록 가져오기 엔티티
data class NuboCommentListResponse(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: NuboCommentListResult
)

// 댓글 목록 Result 엔티티
data class NuboCommentListResult(
    val boardUid: Int,
    val sinceUid: Int,
    val totalCommentCount: Int,
    val comments: List<NuboComment>
)

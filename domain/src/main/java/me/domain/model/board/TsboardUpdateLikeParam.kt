package me.domain.model.board

// 게시글 or 댓글의 좋아요 업데이트에 필요한 파라미터 정의
data class TsboardUpdateLikeParam(
    val boardUid: Int,
    val targetUid: Int,
    val liked: Int,
    val token: String
)
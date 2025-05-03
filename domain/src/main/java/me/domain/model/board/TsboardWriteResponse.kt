package me.domain.model.board

// 댓글 작성 후 받은 응답 엔티티
data class TsboardWriteResponse(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: Int
)
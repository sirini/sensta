package me.data.remote.dto.board

import kotlinx.serialization.Serializable
import me.domain.model.board.TsboardCommentWriteResponse

// 댓글 작성 후 JSON 응답
@Serializable
data class CommentWriteResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: Int
)

// 댓글 작성 후 JSON 응답을 엔티티로 변환하는 매퍼
fun CommentWriteResponseDto.toEntity(): TsboardCommentWriteResponse = TsboardCommentWriteResponse(
    success = success,
    error = error,
    code = code,
    result = result
)
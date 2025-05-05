package me.data.remote.dto.board

import kotlinx.serialization.Serializable
import me.domain.model.board.TsboardWriteResponse

// 댓글 작성 후 JSON 응답
@Serializable
data class WriteResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: Int
)

// 댓글 작성 후 JSON 응답을 엔티티로 변환하는 매퍼
fun WriteResponseDto.toEntity() = TsboardWriteResponse(
    success = success,
    error = error,
    code = code,
    result = result
)
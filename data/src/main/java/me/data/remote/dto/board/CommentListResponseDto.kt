package me.data.remote.dto.board

import kotlinx.serialization.Serializable
import me.domain.model.board.CommentListResponse
import me.domain.model.board.CommentListResult

// 댓글 목록 가져오기 JSON 응답 정의
@Serializable
data class CommentListResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: CommentListResultDto
)

// Result JSON 응답 정의
@Serializable
data class CommentListResultDto(
    val boardUid: Int,
    val sinceUid: Int,
    val totalCommentCount: Int,
    val comments: List<CommentDto>
)

// 댓글 목록 JSON 응답을 엔티티로 변환하는 매퍼
fun CommentListResponseDto.toEntity(): CommentListResponse = CommentListResponse(
    success = success,
    error = error,
    code = code,
    result = result.toEntity()
)

// 댓글 목록 Result 엔티티
fun CommentListResultDto.toEntity(): CommentListResult = CommentListResult(
    boardUid = boardUid,
    sinceUid = sinceUid,
    totalCommentCount = totalCommentCount,
    comments = comments.map { it.toEntity() }
)
package me.data.remote.dto

import kotlinx.serialization.Serializable
import me.domain.model.board.BoardListResponse
import me.domain.model.board.BoardListResult

// 게시글 목록 가져오기 JSON 응답 정의
@Serializable
data class BoardListResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: BoardListResultDto
)

// Result JSON 응답 정의
@Serializable
data class BoardListResultDto(
    val totalPostCount: Int,
    val config: ConfigDto,
    val notices: List<PostDto>,
    val posts: List<PostDto>,
    val blackList: List<Int>,
    val isAdmin: Boolean
)

// 게시글 목록 가져오기 전체 응답을 엔티티로 변환하는 매퍼
fun BoardListResponseDto.toEntity(): BoardListResponse = BoardListResponse(
    success = success,
    error = error,
    code = code,
    result = result.toEntity()
)

// Result 응답을 엔티티로 변환하는 매퍼
fun BoardListResultDto.toEntity(): BoardListResult = BoardListResult(
    totalPostCount = totalPostCount,
    config = config.toEntity(),
    notices = notices.map { it.toEntity() },
    posts = posts.map { it.toEntity() },
    blackList = blackList,
    isAdmin = isAdmin
)
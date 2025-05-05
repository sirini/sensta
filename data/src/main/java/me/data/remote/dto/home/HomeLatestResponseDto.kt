package me.data.remote.dto.home

import kotlinx.serialization.Serializable
import me.data.remote.dto.board.ConfigDto
import me.data.remote.dto.board.toEntity
import me.domain.model.home.TsboardHomeLatestResponse
import me.domain.model.home.TsboardHomeLatestResult

// 지정된 게시판의 최신글 가져오는 JSON 응답
@Serializable
data class HomeLatestResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: HomeLatestResultDto
)

// 지정된 게시판의 최신글 결과 JSON
@Serializable
data class HomeLatestResultDto(
    val items: List<LatestPostDto>,
    val config: ConfigDto
)

// 지정된 게시판의 최신글을 엔티티로 변환하는 매퍼
fun HomeLatestResponseDto.toEntity() = TsboardHomeLatestResponse(
    success = success,
    error = error,
    code = code,
    result = result.toEntity()
)

// 지정된 게시판의 최신글 결과를 엔티티로 변환하는 매퍼
fun HomeLatestResultDto.toEntity() = TsboardHomeLatestResult(
    items = items.map { it.toEntity() },
    config = config.toEntity()
)
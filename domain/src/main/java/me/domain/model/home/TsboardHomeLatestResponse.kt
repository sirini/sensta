package me.domain.model.home

import me.domain.model.board.TsboardConfig

// 지정된 게시판의 최근 게시글 응답 엔티티
data class TsboardHomeLatestResponse(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: TsboardHomeLatestResult
)

// 지정된 게시판의 최근 게시글 Result 엔티티
data class TsboardHomeLatestResult(
    val items: List<TsboardLatestPost>,
    val config: TsboardConfig
)
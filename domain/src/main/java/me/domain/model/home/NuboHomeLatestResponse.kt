package me.domain.model.home

import me.domain.model.board.NuboConfig

// 지정된 게시판의 최근 게시글 응답 엔티티
data class NuboHomeLatestResponse(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: NuboHomeLatestResult
)

// 지정된 게시판의 최근 게시글 Result 엔티티
data class NuboHomeLatestResult(
    val items: List<NuboLatestPost>,
    val config: NuboConfig
)

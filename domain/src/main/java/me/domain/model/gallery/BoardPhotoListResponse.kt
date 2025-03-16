package me.domain.model.gallery

import me.domain.model.board.TsboardConfig

// 갤러리 목록 가져오기 엔티티
data class BoardPhotoListResponse(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: BoardPhotoListResult
)

// Result 엔티티
data class BoardPhotoListResult(
    val totalPostCount: Int,
    val config: TsboardConfig,
    val images: List<TsboardPhoto>
)
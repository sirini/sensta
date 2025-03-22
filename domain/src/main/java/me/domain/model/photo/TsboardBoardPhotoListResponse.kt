package me.domain.model.photo

import me.domain.model.board.TsboardConfig

// 갤러리 목록 가져오기 엔티티
data class TsboardBoardPhotoListResponse(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: TsboardBoardPhotoListResult
)

// Result 엔티티
data class TsboardBoardPhotoListResult(
    val totalPostCount: Int,
    val config: TsboardConfig,
    val images: List<TsboardPhoto>
)
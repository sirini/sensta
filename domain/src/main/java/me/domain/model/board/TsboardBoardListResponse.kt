package me.domain.model.board

// 게시글 목록 가져오기 엔티티
data class TsboardBoardListResponse(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: TsboardBoardListResult
)

// 게시글 목록 Result 엔티티
data class TsboardBoardListResult(
    val totalPostCount: Int,
    val config: TsboardConfig,
    val notices: List<TsboardPost>,
    val posts: List<TsboardPost>,
    val blackList: List<Int>,
    val isAdmin: Boolean
)
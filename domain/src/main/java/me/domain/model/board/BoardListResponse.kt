package me.domain.model.board

// 게시글 목록 가져오기 엔티티
data class BoardListResponse(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: BoardListResult
)

// Result 엔티티
data class BoardListResult(
    val totalPostCount: Int,
    val config: TsboardConfig,
    val notices: List<TsboardPost>,
    val posts: List<TsboardPost>,
    val blackList: List<Int>,
    val isAdmin: Boolean
)
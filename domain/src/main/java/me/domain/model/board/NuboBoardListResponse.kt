package me.domain.model.board

// 게시글 목록 가져오기 엔티티
data class NuboBoardListResponse(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: NuboBoardListResult
)

// 게시글 목록 Result 엔티티
data class NuboBoardListResult(
    val totalPostCount: Int,
    val config: NuboConfig,
    val notices: List<NuboPost>,
    val posts: List<NuboPost>,
    val blackList: List<Int>,
    val isAdmin: Boolean
)

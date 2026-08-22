package me.domain.model.board

// 최근 사용된 해시태그 조회 시 응답 엔티티
data class NuboRecentHashtagResponse(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: List<NuboRecentHashtag>
)

// 최근 사용된 해시태그 결과 엔티티
data class NuboRecentHashtag(
    val uid: Int,
    val name: String,
    val postUid: Int
)

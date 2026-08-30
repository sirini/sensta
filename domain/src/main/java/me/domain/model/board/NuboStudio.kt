package me.domain.model.board

import java.time.LocalDateTime

/** 내 작품 스튜디오에서 지원하는 서버 정렬 방식이다. */
enum class NuboStudioSort(val queryValue: String) {
    Recent("recent"),
    Views("views"),
    Likes("likes"),
    Comments("comments")
}

data class NuboStudioSummary(
    val postCount: Long = 0,
    val photoCount: Long = 0,
    val viewCount: Long = 0,
    val likeCount: Long = 0,
    val commentCount: Long = 0
)

data class NuboStudioPost(
    val uid: Int,
    val title: String,
    val cover: String,
    val submitted: LocalDateTime,
    val modified: LocalDateTime,
    val status: Int,
    val imageCount: Long,
    val hit: Long,
    val like: Long,
    val comment: Long
)

data class NuboStudioPosts(
    val page: Int,
    val limit: Int,
    val totalCount: Long,
    val hasNext: Boolean,
    val items: List<NuboStudioPost>
)

data class NuboStudio(
    val summary: NuboStudioSummary = NuboStudioSummary(),
    val posts: NuboStudioPosts = NuboStudioPosts(
        page = 1,
        limit = 20,
        totalCount = 0,
        hasNext = false,
        items = emptyList()
    )
)

data class NuboStudioParam(
    val page: Int = 1,
    val limit: Int = 20,
    val sort: NuboStudioSort = NuboStudioSort.Recent,
    val token: String
)

package me.data.remote.dto.board

import kotlinx.serialization.Serializable
import me.domain.model.board.NuboStudio
import me.domain.model.board.NuboStudioPost
import me.domain.model.board.NuboStudioPosts
import me.domain.model.board.NuboStudioSummary
import java.time.Instant
import java.time.ZoneOffset

@Serializable
data class StudioResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: StudioResultDto? = null
)

@Serializable
data class StudioResultDto(
    val summary: StudioSummaryDto,
    val posts: StudioPostsDto
)

@Serializable
data class StudioSummaryDto(
    val postCount: Long,
    val photoCount: Long,
    val viewCount: Long,
    val likeCount: Long,
    val commentCount: Long
)

@Serializable
data class StudioPostsDto(
    val page: Int,
    val limit: Int,
    val totalCount: Long,
    val hasNext: Boolean,
    val items: List<StudioPostDto>
)

@Serializable
data class StudioPostDto(
    val uid: Int,
    val title: String,
    val cover: String,
    val submitted: Long,
    val modified: Long,
    val status: Int,
    val imageCount: Long,
    val hit: Long,
    val like: Long,
    val comment: Long
)

fun StudioResponseDto.toEntity(): NuboStudio = requireNotNull(result) {
    "내 작품 스튜디오 응답에 result가 없습니다."
}.toEntity()

private fun StudioResultDto.toEntity() = NuboStudio(
    summary = NuboStudioSummary(
        postCount = summary.postCount,
        photoCount = summary.photoCount,
        viewCount = summary.viewCount,
        likeCount = summary.likeCount,
        commentCount = summary.commentCount
    ),
    posts = NuboStudioPosts(
        page = posts.page,
        limit = posts.limit,
        totalCount = posts.totalCount,
        hasNext = posts.hasNext,
        items = posts.items.map { it.toEntity() }
    )
)

private fun StudioPostDto.toEntity() = NuboStudioPost(
    uid = uid,
    title = title,
    cover = cover,
    submitted = submitted.toKoreanDateTime(),
    modified = modified.toKoreanDateTime(),
    status = status,
    imageCount = imageCount,
    hit = hit,
    like = like,
    comment = comment
)

private fun Long.toKoreanDateTime() =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.ofHours(9)).toLocalDateTime()

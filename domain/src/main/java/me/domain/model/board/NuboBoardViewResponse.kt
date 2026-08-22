package me.domain.model.board

import me.domain.model.common.NuboAttachment
import me.domain.model.common.NuboTag
import me.domain.model.photo.NuboImage
import java.time.LocalDateTime

// 게시글 보기 응답 엔티티
data class NuboBoardViewResponse(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: NuboBoardViewResult
)

// 게시글 보기 Result 엔티티
data class NuboBoardViewResult(
    val config: NuboConfig,
    val post: NuboPost,
    val images: List<NuboImage>,
    val files: List<NuboAttachment>,
    val tags: List<NuboTag>,
    val prevPostUid: Int,
    val nextPostUid: Int,
    val writerPosts: List<NuboBoardViewWriterLatestPost>,
    val writerComments: List<NuboBoardViewWriterLatestComment>
)

// 게시글 작성자의 최근글 엔티티
data class NuboBoardViewWriterLatestPost(
    val board: NuboBasicConfig,
    val postUid: Int,
    val like: Int,
    val submitted: LocalDateTime,
    val comment: Int,
    val title: String
)

// 게시글 작성자의 최근 댓글 엔티티
data class NuboBoardViewWriterLatestComment(
    val board: NuboBasicConfig,
    val postUid: Int,
    val like: Int,
    val submitted: LocalDateTime,
    val content: String
)

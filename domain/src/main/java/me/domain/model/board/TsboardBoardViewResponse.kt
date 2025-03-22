package me.domain.model.board

import me.domain.model.common.TsboardAttachment
import me.domain.model.common.TsboardTag
import me.domain.model.photo.TsboardImage
import java.time.LocalDateTime

// 게시글 보기 응답 엔티티
data class TsboardBoardViewResponse(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: TsboardBoardViewResult
)

// 게시글 보기 Result 엔티티
data class TsboardBoardViewResult(
    val config: TsboardConfig,
    val post: TsboardPost,
    val images: List<TsboardImage>,
    val files: List<TsboardAttachment>,
    val tags: List<TsboardTag>,
    val prevPostUid: Int,
    val nextPostUid: Int,
    val writerPosts: List<TsboardBoardViewWriterLatestPost>,
    val writerComments: List<TsboardBoardViewWriterLatestComment>
)

// 게시글 작성자의 최근글 엔티티
data class TsboardBoardViewWriterLatestPost(
    val board: TsboardBasicConfig,
    val postUid: Int,
    val like: Int,
    val submitted: LocalDateTime,
    val comment: Int,
    val title: String
)

// 게시글 작성자의 최근 댓글 엔티티
data class TsboardBoardViewWriterLatestComment(
    val board: TsboardBasicConfig,
    val postUid: Int,
    val like: Int,
    val submitted: LocalDateTime,
    val content: String
)

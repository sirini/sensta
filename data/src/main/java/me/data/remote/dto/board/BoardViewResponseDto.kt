package me.data.remote.dto.board

import kotlinx.serialization.Serializable
import me.data.remote.dto.common.AttachmentDto
import me.data.remote.dto.common.TagDto
import me.data.remote.dto.common.toEntity
import me.data.remote.dto.photo.ImageDto
import me.data.remote.dto.photo.toEntity
import me.domain.model.board.TsboardBoardViewResponse
import me.domain.model.board.TsboardBoardViewResult
import me.domain.model.board.TsboardBoardViewWriterLatestComment
import me.domain.model.board.TsboardBoardViewWriterLatestPost
import java.time.Instant
import java.time.ZoneOffset

// 게시글 열람하기 JSON 응답
@Serializable
data class BoardViewResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: BoardViewResultDto
)

// 게시글 보기 Result JSON 응답
@Serializable
data class BoardViewResultDto(
    val config: ConfigDto,
    val post: PostDto,
    val images: List<ImageDto>,
    val files: List<AttachmentDto>,
    val tags: List<TagDto>,
    val prevPostUid: Int,
    val nextPostUid: Int,
    val writerPosts: List<BoardViewWriterLatestPostDto>,
    val writerComments: List<BoardViewWriterLatestCommentDto>
)

// 게시글 작성자의 최근글 JSON 응답
@Serializable
data class BoardViewWriterLatestPostDto(
    val board: BasicConfigDto,
    val postUid: Int,
    val like: Int,
    val submitted: Long,
    val comment: Int,
    val title: String
)

// 게시글 작성자의 최근 댓글 JSON 응답
@Serializable
data class BoardViewWriterLatestCommentDto(
    val board: BasicConfigDto,
    val postUid: Int,
    val like: Int,
    val submitted: Long,
    val content: String
)

// 게시글 열람하기 결과를 엔티티로 변환하는 매퍼
fun BoardViewResponseDto.toEntity() = TsboardBoardViewResponse(
    success = success,
    error = error,
    code = code,
    result = result.toEntity()
)

// 게시글 보기 Result 응답을 엔티티로 변환하는 매퍼
fun BoardViewResultDto.toEntity() = TsboardBoardViewResult(
    config = config.toEntity(),
    post = post.toEntity(),
    images = images.map { it.toEntity() },
    files = files.map { it.toEntity() },
    tags = tags.map { it.toEntity() },
    prevPostUid = prevPostUid,
    nextPostUid = nextPostUid,
    writerPosts = writerPosts.map { it.toEntity() },
    writerComments = writerComments.map { it.toEntity() }
)

// 게시글 작성자의 최근글 응답을 엔티티로 변환하는 매퍼
fun BoardViewWriterLatestPostDto.toEntity() = TsboardBoardViewWriterLatestPost(
    board = board.toEntity(),
    postUid = postUid,
    like = like,
    submitted = Instant.ofEpochMilli(submitted).atZone(ZoneOffset.UTC).toLocalDateTime(),
    comment = comment,
    title = title
)

// 게시글 작성자의 최근 댓글 응답을 엔티티로 변환하는 매퍼
fun BoardViewWriterLatestCommentDto.toEntity() = TsboardBoardViewWriterLatestComment(
    board = board.toEntity(),
    postUid = postUid,
    like = like,
    submitted = Instant.ofEpochMilli(submitted).atZone(ZoneOffset.UTC).toLocalDateTime(),
    content = content
)
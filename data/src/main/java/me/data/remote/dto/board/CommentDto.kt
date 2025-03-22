package me.data.remote.dto.board

import kotlinx.serialization.Serializable
import me.data.remote.dto.common.WriterDto
import me.data.remote.dto.common.toEntity
import me.domain.model.board.TsboardComment
import java.time.Instant
import java.time.ZoneOffset

// 댓글 JSON 응답 정의
@Serializable
data class CommentDto(
    val uid: Int,
    val replyUid: Int,
    val postUid: Int,
    val writer: WriterDto,
    val like: Int,
    val liked: Boolean,
    val submitted: Long,
    val modified: Long,
    val status: Int,
    val content: String
)

// 댓글 JSON 응답을 엔티티로 변환하는 매퍼
fun CommentDto.toEntity(): TsboardComment = TsboardComment(
    uid = uid,
    replyUid = replyUid,
    postUid = postUid,
    writer = writer.toEntity(),
    like = like,
    liked = liked,
    submitted = Instant.ofEpochMilli(submitted).atZone(ZoneOffset.ofHours(9)).toLocalDateTime(),
    status = status,
    content = content
)
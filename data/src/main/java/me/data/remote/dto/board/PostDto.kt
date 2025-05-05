package me.data.remote.dto.board

import kotlinx.serialization.Serializable
import me.data.remote.dto.common.WriterDto
import me.data.remote.dto.common.toEntity
import me.domain.model.board.TsboardCategory
import me.domain.model.board.TsboardPost
import java.time.Instant
import java.time.ZoneOffset

// 게시글 JSON 응답 정의
@Serializable
data class PostDto(
    val uid: Int,
    val title: String,
    val content: String,
    val submitted: Long,
    val modified: Long,
    val hit: Int,
    val status: Int,
    val category: CategoryDto,
    val cover: String,
    val comment: Int,
    val like: Int,
    val liked: Boolean,
    val writer: WriterDto
)

// 게시글 카테고리 JSON 응답 정의
@Serializable
data class CategoryDto(
    val uid: Int,
    val name: String
)

// 게시글 JSON 응답을 엔티티로 변환하는 매퍼
fun PostDto.toEntity() = TsboardPost(
    uid = uid,
    title = title,
    content = content,
    submitted = Instant.ofEpochMilli(submitted).atZone(ZoneOffset.ofHours(9)).toLocalDateTime(),
    hit = hit,
    status = status,
    category = category.toEntity(),
    cover = cover,
    comment = comment,
    like = like,
    liked = liked,
    writer = writer.toEntity()
)

// 게시글 카테고리 JSON 응답을 엔티티로 변환하는 매퍼
fun CategoryDto.toEntity() = TsboardCategory(
    uid = uid,
    name = name
)


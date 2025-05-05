package me.data.remote.dto.home

import kotlinx.serialization.Serializable
import me.data.remote.dto.board.CategoryDto
import me.data.remote.dto.board.toEntity
import me.data.remote.dto.common.WriterDto
import me.data.remote.dto.common.toEntity
import me.domain.model.home.TsboardLatestPost
import java.time.Instant
import java.time.ZoneOffset

// 최근글 JSON 응답 정의
@Serializable
data class LatestPostDto(
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
    val writer: WriterDto,
    val id: String,
    val type: Int,
    val useCategory: Boolean
)

// 최근글 응답을 엔티티로 변환
fun LatestPostDto.toEntity() = TsboardLatestPost(
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
    writer = writer.toEntity(),
    id = id,
    type = type,
    useCategory = useCategory
)
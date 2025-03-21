package me.data.remote.dto.photo

import kotlinx.serialization.Serializable
import me.data.remote.dto.common.WriterDto
import me.data.remote.dto.common.toEntity
import me.domain.model.photo.TsboardPhoto
import me.domain.model.photo.TsboardThumbnail

// 갤러리 리스트의 JSON 응답 정의
@Serializable
data class PhotoDto(
    val uid: Int,
    val like: Int,
    val liked: Boolean,
    val writer: WriterDto,
    val comment: Int,
    val title: String,
    val images: List<ImageDto>
)

// 첨부 이미지의 크고 작은 썸네일 경로 JSON 응답 정의
@Serializable
data class PhotoImageThumbnailDto(
    val large: String,
    val small: String
)

// 갤러리 리스트의 JSON 응답을 엔티티로 변환하는 매퍼
fun PhotoDto.toEntity(): TsboardPhoto = TsboardPhoto(
    uid = uid,
    like = like,
    liked = liked,
    writer = writer.toEntity(),
    comment = comment,
    title = title,
    images = images.map { it.toEntity() }
)

// 썸네일을 엔티티로 변환하는 매퍼
fun PhotoImageThumbnailDto.toEntity(): TsboardThumbnail = TsboardThumbnail(
    large = large,
    small = small
)

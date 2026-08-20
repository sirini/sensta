package me.data.remote.dto.photo

import kotlinx.serialization.Serializable
import me.data.remote.dto.common.ImageFileDto
import me.data.remote.dto.common.toEntity
import me.domain.model.photo.TsboardImage

// 썸네일 이미지, AI 설명 JSON 응답 정의
@Serializable
data class ImageDto(
    val file: ImageFileDto,
    val thumbnail: PhotoImageThumbnailDto,
    val exif: ExifDto,
    val description: String
)

// 첨부 이미지의 크기별 썸네일 경로
@Serializable
data class PhotoImageThumbnailDto(
    val large: String,
    val small: String
)

// 갤러리의 개별 게시글에 대한 첨부 이미지를 엔티티로 변환하는 매퍼
fun ImageDto.toEntity() = TsboardImage(
    file = file.toEntity(),
    thumbnail = thumbnail.toEntity(),
    exif = exif.toEntity(),
    description = description
)

// 썸네일을 엔티티로 변환
fun PhotoImageThumbnailDto.toEntity() = me.domain.model.photo.TsboardThumbnail(
    large = large,
    small = small
)

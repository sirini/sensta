package me.data.remote.dto

import kotlinx.serialization.Serializable
import me.domain.model.board.TsboardExif
import me.domain.model.board.TsboardFile
import me.domain.model.board.TsboardImage
import me.domain.model.board.TsboardThumbnail
import me.domain.model.gallery.TsboardPhoto
import java.time.Instant
import java.time.ZoneOffset

// 갤러리 리스트의 JSON 응답 정의
@Serializable
data class PhotoDto(
    val uid: Int,
    val like: Int,
    val liked: Boolean,
    val writer: WriterDto,
    val comment: Int,
    val title: String,
    val images: List<PhotoImageDto>
)

// 갤러리의 개별 게시글에 대한 첨부 이미지 JSON 응답 정의
@Serializable
data class PhotoImageDto(
    val file: PhotoImageFileDto,
    val thumbnail: PhotoImageThumbnailDto,
    val exif: PhotoImageExifDto,
    val description: String
)

// 첨부 파일 원본 경로 및 고유번호 JSON 응답 정의
@Serializable
data class PhotoImageFileDto(
    val uid: Int,
    val path: String
)

// 첨부 이미지의 크고 작은 썸네일 경로 JSON 응답 정의
@Serializable
data class PhotoImageThumbnailDto(
    val large: String,
    val small: String
)

// 첨부 이미지에 대한 EXIF 정보 JSON 응답 정의
@Serializable
data class PhotoImageExifDto(
    val make: String,
    val model: String,
    val aperture: Int,
    val iso: Int,
    val focalLength: Int,
    val exposure: Int,
    val width: Int,
    val height: Int,
    val date: Long
)

// 갤러리 JSON 응답을 엔티티로 변환하는 매퍼
fun PhotoDto.toEntity(): TsboardPhoto = TsboardPhoto(
    uid = uid,
    like = like,
    liked = liked,
    writer = writer.toEntity(),
    comment = comment,
    title = title,
    images = images.map { it.toEntity() }
)

// 갤러리의 개별 게시글에 대한 첨부 이미지를 엔티티로 변환하는 매퍼
fun PhotoImageDto.toEntity(): TsboardImage = TsboardImage(
    file = file.toEntity(),
    thumbnail = thumbnail.toEntity(),
    exif = exif.toEntity(),
    description = description
)

// 첨부파일을 엔티티로 변환하는 매퍼
fun PhotoImageFileDto.toEntity(): TsboardFile = TsboardFile(
    uid = uid,
    path = path
)

// 썸네일을 엔티티로 변환하는 매퍼
fun PhotoImageThumbnailDto.toEntity(): TsboardThumbnail = TsboardThumbnail(
    large = large,
    small = small
)

// EXIF를 엔티티로 변환하는 매퍼
fun PhotoImageExifDto.toEntity(): TsboardExif = TsboardExif(
    make = make,
    model = model,
    aperture = aperture,
    iso = iso,
    focalLength = focalLength,
    exposure = exposure,
    width = width,
    height = height,
    date = Instant.ofEpochMilli(date).atZone(ZoneOffset.UTC).toLocalDateTime()
)
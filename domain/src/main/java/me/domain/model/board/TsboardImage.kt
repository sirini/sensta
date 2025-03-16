package me.domain.model.board

import java.time.LocalDateTime

// 이미지 첨부파일 엔티티
data class TsboardImage(
    val file: TsboardFile,
    val thumbnail: TsboardThumbnail,
    val exif: TsboardExif,
    val description: String
)

// 원래 첨부파일 엔티티
data class TsboardFile(
    val uid: Int,
    val path: String
)

// 썸네일 엔티티
data class TsboardThumbnail(
    val large: String,
    val small: String
)

// EXIF 엔티티
data class TsboardExif(
    val make: String,
    val model: String,
    val aperture: Int,
    val iso: Int,
    val focalLength: Int,
    val exposure: Int,
    val width: Int,
    val height: Int,
    val date: LocalDateTime
)
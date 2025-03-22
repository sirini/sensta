package me.domain.model.photo

import me.domain.model.common.TsboardImageFile

// 이미지 첨부파일 엔티티
data class TsboardImage(
    val file: TsboardImageFile,
    val thumbnail: TsboardThumbnail,
    val exif: TsboardExif,
    val description: String
)

// 썸네일 엔티티
data class TsboardThumbnail(
    val large: String,
    val small: String
)


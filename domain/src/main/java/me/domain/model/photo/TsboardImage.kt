package me.domain.model.photo

import me.domain.model.common.TsboardFile

// 이미지 첨부파일 엔티티
data class TsboardImage(
    val file: TsboardFile,
    val thumbnail: TsboardThumbnail,
    val exif: TsboardExif,
    val description: String
)

// 썸네일 엔티티
data class TsboardThumbnail(
    val large: String,
    val small: String
)


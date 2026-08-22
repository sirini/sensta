package me.domain.model.photo

import me.domain.model.common.NuboImageFile

// 이미지 첨부파일 엔티티
data class NuboImage(
    val file: NuboImageFile,
    val thumbnail: NuboThumbnail,
    val exif: NuboExif,
    val description: String
)

// 썸네일 엔티티
data class NuboThumbnail(
    val large: String,
    val small: String
)

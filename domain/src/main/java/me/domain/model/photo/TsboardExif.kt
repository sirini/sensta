package me.domain.model.photo

import java.time.LocalDateTime

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
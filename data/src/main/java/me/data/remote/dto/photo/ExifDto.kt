package me.data.remote.dto.photo

import kotlinx.serialization.Serializable
import me.domain.model.photo.TsboardExif
import java.time.Instant
import java.time.ZoneOffset

// EXIF JSON 응답 정의
@Serializable
data class ExifDto(
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

// EXIF를 엔티티로 변환하는 매퍼
fun ExifDto.toEntity(): TsboardExif = TsboardExif(
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
package me.data.remote.dto

import kotlinx.serialization.Serializable
import me.domain.model.gallery.BoardPhotoListResponse
import me.domain.model.gallery.BoardPhotoListResult

// 갤러리 목록 가져오기 JSON 응답 정의
@Serializable
data class BoardPhotoListResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: BoardPhotoListResultDto
)

// Result JSON 응답 정의
@Serializable
data class BoardPhotoListResultDto(
    val totalPostCount: Int,
    val config: ConfigDto,
    val images: List<PhotoDto>
)

// 갤러리 목록 가져오기 엔티티
fun BoardPhotoListResponseDto.toEntity(): BoardPhotoListResponse = BoardPhotoListResponse(
    success = success,
    error = error,
    code = code,
    result = result.toEntity()
)

// Result 엔티티
fun BoardPhotoListResultDto.toEntity(): BoardPhotoListResult = BoardPhotoListResult(
    totalPostCount = totalPostCount,
    config = config.toEntity(),
    images = images.map { it.toEntity() }
)
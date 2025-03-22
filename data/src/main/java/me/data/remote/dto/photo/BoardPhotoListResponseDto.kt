package me.data.remote.dto.photo

import kotlinx.serialization.Serializable
import me.data.remote.dto.board.ConfigDto
import me.data.remote.dto.board.toEntity
import me.domain.model.photo.TsboardBoardPhotoListResponse
import me.domain.model.photo.TsboardBoardPhotoListResult

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
fun BoardPhotoListResponseDto.toEntity(): TsboardBoardPhotoListResponse =
    TsboardBoardPhotoListResponse(
        success = success,
        error = error,
        code = code,
        result = result.toEntity()
    )

// Result 엔티티
fun BoardPhotoListResultDto.toEntity(): TsboardBoardPhotoListResult = TsboardBoardPhotoListResult(
    totalPostCount = totalPostCount,
    config = config.toEntity(),
    images = images.map { it.toEntity() }
)
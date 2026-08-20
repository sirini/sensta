package me.data.remote.dto.common

import kotlinx.serialization.Serializable
import me.domain.model.common.TsboardResponseNothing

@Serializable
data class BooleanResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: Boolean? = null
)

// 기존 도메인 경계를 유지하면서 최신 API의 불리언 결과를 문자열로 전달한다.
fun BooleanResponseDto.toEntity() = TsboardResponseNothing(
    success = success,
    error = error,
    code = code,
    result = result?.toString()
)

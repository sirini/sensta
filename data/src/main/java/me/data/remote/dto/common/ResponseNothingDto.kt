package me.data.remote.dto.common

import kotlinx.serialization.Serializable
import me.domain.model.common.ResponseNothing

// result에 아무것도 없을 때 사용하는 DTO
@Serializable
data class ResponseNothingDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: String? = null
)

// result에 값이 있을 때 사용하는 엔티티 매퍼
fun ResponseNothingDto.toEntity(): ResponseNothing = ResponseNothing(
    success = success,
    error = error,
    code = code,
    result = result
)
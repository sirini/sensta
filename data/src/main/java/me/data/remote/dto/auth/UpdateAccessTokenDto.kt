package me.data.remote.dto.auth

import kotlinx.serialization.Serializable
import me.domain.model.auth.TsboardUpdateAccessToken

// 리프레시 토큰으로 새 액세스 토큰 발급 JSON 응답
@Serializable
data class UpdateAccessTokenDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: String? = null
)

// 액세스 토큰 응답 엔티티로 변환하는 매퍼
fun UpdateAccessTokenDto.toEntity(): TsboardUpdateAccessToken = TsboardUpdateAccessToken(
    success = success,
    error = error,
    code = code,
    result = result
)
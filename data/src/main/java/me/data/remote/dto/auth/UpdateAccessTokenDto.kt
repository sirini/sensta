package me.data.remote.dto.auth

import kotlinx.serialization.Serializable
import me.domain.model.auth.NuboAuthTokenPair
import me.domain.model.auth.NuboUpdateAccessToken

// 리프레시 토큰으로 새 액세스 토큰 발급 JSON 응답
@Serializable
data class UpdateAccessTokenDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: AuthTokenPairDto? = null
)

@Serializable
data class AuthTokenPairDto(
    val token: String,
    val refresh: String
)

@Serializable
data class MobileRefreshRequestDto(val refresh: String)

// 액세스 토큰 응답 엔티티로 변환하는 매퍼
fun UpdateAccessTokenDto.toEntity() = NuboUpdateAccessToken(
    success = success,
    error = error,
    code = code,
    result = result?.let { NuboAuthTokenPair(token = it.token, refresh = it.refresh) }
)

package me.data.remote.dto.auth

import kotlinx.serialization.Serializable
import me.domain.model.auth.NuboSignupStatus

@Serializable
data class SignupStatusResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: SignupStatusDto? = null
)

@Serializable
data class SignupStatusDto(
    val mode: String,
    val mailConfigured: Boolean,
    val oauthRegistrationAllowed: Boolean
)

fun SignupStatusDto.toEntity() = NuboSignupStatus(
    mode = mode,
    mailConfigured = mailConfigured,
    oauthRegistrationAllowed = oauthRegistrationAllowed
)

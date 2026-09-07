package me.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class PasswordResetRequestDto(val email: String)

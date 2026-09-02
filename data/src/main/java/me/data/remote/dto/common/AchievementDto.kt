package me.data.remote.dto.common

import kotlinx.serialization.Serializable

@Serializable
data class AchievementListResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: List<BadgeDto> = emptyList()
)

@Serializable
data class AchievementAcknowledgeRequestDto(
    val keys: List<String>
)

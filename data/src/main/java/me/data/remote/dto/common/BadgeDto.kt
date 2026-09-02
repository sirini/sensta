package me.data.remote.dto.common

import kotlinx.serialization.Serializable
import me.domain.model.common.NuboBadge

@Serializable
data class BadgeDto(
    val key: String,
    val name: String,
    val description: String,
    val iconKey: String,
    val earnedAt: Long
)

fun BadgeDto.toEntity() = NuboBadge(
    key = key,
    name = name,
    description = description,
    iconKey = iconKey,
    earnedAt = earnedAt
)

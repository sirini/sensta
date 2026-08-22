package me.data.remote.dto.user

import kotlinx.serialization.Serializable
import me.domain.model.user.NuboUserSafetyStatus

@Serializable
data class UserSafetyStatusResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: UserSafetyStatusDto? = null
)

@Serializable
data class UserSafetyStatusDto(
    val isReported: Boolean,
    val isBannedByMe: Boolean
)

@Serializable
data class UserReportRequestDto(
    val targetUserUid: Int,
    val checkedBlackList: Boolean,
    val content: String
)

@Serializable
data class UserTargetRequestDto(
    val targetUserUid: Int
)

fun UserSafetyStatusDto.toEntity() = NuboUserSafetyStatus(
    isReported = isReported,
    isBlockedByMe = isBannedByMe
)

package me.domain.model.user

// 현재 사용자가 상대방에게 취한 안전 조치 상태
data class NuboUserSafetyStatus(
    val isReported: Boolean,
    val isBlockedByMe: Boolean
)

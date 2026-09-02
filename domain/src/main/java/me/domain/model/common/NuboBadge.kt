package me.domain.model.common

// 사용자가 한 번 획득하면 계속 유지되는 커뮤니티 업적
data class NuboBadge(
    val key: String,
    val name: String,
    val description: String,
    val iconKey: String,
    val earnedAt: Long
)

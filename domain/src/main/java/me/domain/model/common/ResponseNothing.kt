package me.domain.model.common

// result가 없을 때 사용하는 엔티티
data class ResponseNothing(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: String? = null
)
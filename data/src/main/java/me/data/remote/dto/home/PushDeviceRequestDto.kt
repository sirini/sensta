package me.data.remote.dto.home

import kotlinx.serialization.Serializable

// 모바일 푸시 토큰 등록 및 해제 요청
@Serializable
data class PushDeviceRequestDto(
    val token: String,
    val platform: String
)

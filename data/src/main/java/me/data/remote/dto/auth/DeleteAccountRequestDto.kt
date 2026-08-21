package me.data.remote.dto.auth

import kotlinx.serialization.Serializable

// 실수로 계정을 삭제하지 않도록 서버가 요구하는 확인 문자열
@Serializable
data class DeleteAccountRequestDto(
    val confirmation: String
)

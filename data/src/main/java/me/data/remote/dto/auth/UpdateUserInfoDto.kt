package me.data.remote.dto.auth

import kotlinx.serialization.Serializable
import me.domain.model.auth.TsboardUpdateUserInfo

// 사용자의 정보 업데이트 요청에 대한 JSON 응답
@Serializable
data class UpdateUserInfoDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: String?
)

// 사용자의 정보 업데이트 요청에 대한 응답 DTO를 엔티티로 변환
fun UpdateUserInfoDto.toEntity() = TsboardUpdateUserInfo(
    success = success,
    error = error,
    code = code,
    result = result
)
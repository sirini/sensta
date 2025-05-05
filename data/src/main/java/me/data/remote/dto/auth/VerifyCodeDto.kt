package me.data.remote.dto.auth

import kotlinx.serialization.Serializable
import me.domain.model.auth.TsboardVerifyCode

// 회원가입 시 인증코드 전송 후 확인 요청에 대한 JSON 응답
@Serializable
data class VerifyCodeDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: Boolean
)

// 인증코드 JSON 응답을 엔티티로 변환하는 매퍼
fun VerifyCodeDto.toEntity() = TsboardVerifyCode(
    success = success,
    error = error,
    code = code,
    result = result
)
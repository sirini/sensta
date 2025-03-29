package me.data.remote.dto.auth

import kotlinx.serialization.Serializable
import me.domain.model.auth.TsboardCheckEmail

// 이메일 주소가 이미 등록되어 있는지에 대한 JSON 응답 정의
@Serializable
data class CheckEmailDto(
    val success: Boolean, /* false : 이미 등록된 이메일 주소 */
    val error: String,
    val code: Int,
    val result: String? = null
)

// 이메일 주소 등록 여부를 엔티티로 변환하는 매퍼
fun CheckEmailDto.toEntity(): TsboardCheckEmail = TsboardCheckEmail(
    success = success,
    error = error,
    code = code
)
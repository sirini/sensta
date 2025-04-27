package me.data.remote.dto.auth

import kotlinx.serialization.Serializable
import me.domain.model.auth.TsboardSignup
import me.domain.model.auth.TsboardSignupResult

// 회원가입 후에 받을 JSON 응답 정의
@Serializable
data class SignupDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: SignupResultDto
)

// 회원가입 시 이메일 인증 여부를 포함한 결과 정의
@Serializable
data class SignupResultDto(
    val sendmail: Boolean,
    val target: Int
)

// 회원가입 시 받을 응답을 엔티티로 변환하는 매퍼
fun SignupDto.toEntity(): TsboardSignup = TsboardSignup(
    success = success,
    error = error,
    code = code,
    result = result.toEntity()
)

// 회원가입 시 메일 인증 여부를 포함한 결과를 엔티티로 변환하는 매퍼
fun SignupResultDto.toEntity(): TsboardSignupResult = TsboardSignupResult(
    sendmail = sendmail,
    target = target
)
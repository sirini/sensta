package me.data.remote.dto.auth

import kotlinx.serialization.Serializable
import me.domain.model.auth.TsboardSignin
import me.domain.model.auth.TsboardSigninResult
import java.time.Instant
import java.time.ZoneOffset

// 로그인 후에 받을 JSON 응답 정의
@Serializable
data class SigninDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: SigninResultDto? = null
)

// 로그인 후에 받을 결과 JSON 응답
@Serializable
data class SigninResultDto(
    val uid: Int,
    val name: String,
    val profile: String,
    val level: Int,
    val signature: String,
    val signup: Long,
    val signin: Long,
    val admin: Boolean,
    val blocked: Boolean,
    val id: String,
    val point: Int,
    val token: String,
    val refresh: String
)

// 로그인 후에 받을 전체 응답을 엔티티로 변환하는 매퍼
fun SigninDto.toEntity(): TsboardSignin = TsboardSignin(
    success = success,
    error = error,
    code = code,
    result = result?.toEntity()
)

// 로그인 후에 받을 결과를 엔티티로 변환하는 매퍼
fun SigninResultDto.toEntity(): TsboardSigninResult = TsboardSigninResult(
    uid = uid,
    name = name,
    profile = profile,
    level = level,
    signature = signature,
    signup = Instant.ofEpochMilli(signup).atZone(ZoneOffset.ofHours(9)).toLocalDateTime(),
    signin = Instant.ofEpochMilli(signin).atZone(ZoneOffset.ofHours(9)).toLocalDateTime(),
    admin = admin,
    blocked = blocked,
    id = id,
    point = point,
    token = token,
    refresh = refresh
)
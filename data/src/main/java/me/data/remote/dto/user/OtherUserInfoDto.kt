package me.data.remote.dto.user

import kotlinx.serialization.Serializable
import me.domain.model.user.TsboardOtherUserInfo
import me.domain.model.user.TsboardOtherUserInfoResult
import java.time.Instant
import java.time.ZoneOffset

// 다른 사용자의 기본 정보 JSON 응답
@Serializable
data class OtherUserInfoDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: OtherUserInfoResultDto
)

// 다른 사용자의 기본 정보 결과
@Serializable
data class OtherUserInfoResultDto(
    val uid: Int,
    val name: String,
    val profile: String,
    val level: Int,
    val signature: String,
    val signup: Long,
    val signin: Long,
    val admin: Boolean,
    val blocked: Boolean
)

// 다른 사용자의 기본 정보 응답을 엔티티로 변환하는 매퍼
fun OtherUserInfoDto.toEntity(): TsboardOtherUserInfo = TsboardOtherUserInfo(
    success = success,
    error = error,
    code = code,
    result = result.toEntity()
)

// 다른 사용자의 기본 정보를 엔티티로 변환하는 매퍼
fun OtherUserInfoResultDto.toEntity(): TsboardOtherUserInfoResult = TsboardOtherUserInfoResult(
    uid = uid,
    name = name,
    profile = profile,
    level = level,
    signature = signature,
    signup = Instant.ofEpochMilli(signup).atZone(ZoneOffset.ofHours(9)).toLocalDateTime(),
    signin = Instant.ofEpochMilli(signin).atZone(ZoneOffset.ofHours(9)).toLocalDateTime(),
    admin = admin,
    blocked = blocked
)
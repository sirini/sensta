package me.domain.model.auth

import java.time.LocalDateTime

// 로그인 후에 받을 응답 엔티티
data class TsboardSignin(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: TsboardSigninResult? = null
)

// 로그인 후에 받는 결과 엔티티
data class TsboardSigninResult(
    val uid: Int,
    val name: String,
    val profile: String,
    val level: Int,
    val signature: String,
    val signup: LocalDateTime,
    val signin: LocalDateTime,
    val admin: Boolean,
    val blocked: Boolean,
    val id: String,
    val point: Int,
    val token: String,
    val refresh: String
)

// 사용자 정보 빈 엔티티
val emptyUser = TsboardSigninResult(
    uid = -1,
    name = "",
    profile = "",
    level = 0,
    signature = "",
    signup = LocalDateTime.now(),
    signin = LocalDateTime.now(),
    admin = false,
    blocked = false,
    id = "",
    point = 0,
    token = "",
    refresh = ""
)
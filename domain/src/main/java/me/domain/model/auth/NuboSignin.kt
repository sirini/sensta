package me.domain.model.auth

import java.time.LocalDateTime

// 로그인 후에 받을 응답 엔티티
data class NuboSignin(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: NuboSigninResult? = null
)

// 로그인 후에 받는 결과 엔티티
data class NuboSigninResult(
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

/** 화면에서 로그인 상태로 취급할 수 있는 완전한 세션인지 확인한다. */
val NuboSigninResult.hasCompleteSession: Boolean
    get() = uid > 0 && name.isNotBlank() && token.isNotBlank() && refresh.isNotBlank()

// 사용자 정보 빈 엔티티
val emptyUser = NuboSigninResult(
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

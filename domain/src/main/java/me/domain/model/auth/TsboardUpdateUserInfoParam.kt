package me.domain.model.auth

import okhttp3.MultipartBody

// 사용자의 정보 업데이트하는 파라미터 엔티티
data class TsboardUpdateUserInfoParam(
    val authorization: String,
    val name: String,
    val signature: String,
    val password: String,
    val profile: MultipartBody.Part?
)
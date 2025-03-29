package me.domain.repository

import me.domain.model.auth.TsboardCheckEmail
import me.domain.model.auth.TsboardSignin
import me.domain.model.auth.TsboardSigninResult

// 사용자 인증 관련 인터페이스
interface TsboardAuthRepository {
    suspend fun checkEmail(email: String): TsboardResponse<TsboardCheckEmail>
    suspend fun signin(id: String, password: String): TsboardResponse<TsboardSignin>
    suspend fun saveUserInfo(user: TsboardSigninResult)
    suspend fun getUserInfo(): TsboardSigninResult
    suspend fun clearUserInfo()
}
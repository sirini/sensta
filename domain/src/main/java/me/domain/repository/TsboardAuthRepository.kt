package me.domain.repository

import me.domain.model.auth.TsboardCheckEmail
import me.domain.model.auth.TsboardSignin
import me.domain.model.auth.TsboardSigninResult
import me.domain.model.auth.TsboardUpdateAccessToken
import me.domain.model.auth.TsboardUpdateUserInfo
import me.domain.model.auth.TsboardUpdateUserInfoParam

// 사용자 인증 관련 인터페이스
interface TsboardAuthRepository {
    suspend fun checkEmail(email: String): TsboardResponse<TsboardCheckEmail>
    suspend fun clearUserInfo()
    suspend fun getUserInfo(): TsboardSigninResult
    suspend fun signIn(id: String, password: String): TsboardResponse<TsboardSignin>
    suspend fun signInWithGoogle(idToken: String): TsboardResponse<TsboardSignin>
    suspend fun saveUserInfo(user: TsboardSigninResult)
    suspend fun updateAccessToken(
        userUid: Int,
        refresh: String
    ): TsboardResponse<TsboardUpdateAccessToken>

    suspend fun updateUserInfo(param: TsboardUpdateUserInfoParam): TsboardResponse<TsboardUpdateUserInfo>
}
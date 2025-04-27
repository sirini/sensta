package me.domain.repository

import me.domain.model.auth.TsboardSignin
import me.domain.model.auth.TsboardSigninResult
import me.domain.model.auth.TsboardSignup
import me.domain.model.auth.TsboardUpdateAccessToken
import me.domain.model.auth.TsboardUpdateUserInfo
import me.domain.model.auth.TsboardUpdateUserInfoParam
import me.domain.model.auth.TsboardVerifyCodeParam
import me.domain.model.common.TsboardResponseNothing

// 사용자 인증 관련 인터페이스
interface TsboardAuthRepository {
    suspend fun checkEmail(email: String): TsboardResponse<TsboardResponseNothing>
    suspend fun checkName(name: String): TsboardResponse<TsboardResponseNothing>
    suspend fun clearUserInfo()
    suspend fun getUserInfo(): TsboardSigninResult
    suspend fun signIn(id: String, password: String): TsboardResponse<TsboardSignin>
    suspend fun signInWithGoogle(idToken: String): TsboardResponse<TsboardSignin>
    suspend fun signUp(id: String, password: String, name: String): TsboardResponse<TsboardSignup>
    suspend fun saveUserInfo(user: TsboardSigninResult)
    suspend fun updateAccessToken(
        userUid: Int,
        refresh: String
    ): TsboardResponse<TsboardUpdateAccessToken>

    suspend fun updateUserInfo(param: TsboardUpdateUserInfoParam): TsboardResponse<TsboardUpdateUserInfo>
    suspend fun verifyCode(param: TsboardVerifyCodeParam): TsboardResponse<TsboardResponseNothing>
}
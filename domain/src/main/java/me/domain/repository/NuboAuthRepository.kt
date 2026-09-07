package me.domain.repository

import me.domain.model.auth.NuboSignin
import me.domain.model.auth.NuboSigninResult
import me.domain.model.auth.NuboSignup
import me.domain.model.auth.NuboSignupStatus
import me.domain.model.auth.NuboUpdateAccessToken
import me.domain.model.auth.NuboUpdateUserInfo
import me.domain.model.auth.NuboUpdateUserInfoParam
import me.domain.model.auth.NuboVerifyCodeParam
import me.domain.model.common.NuboResponseNothing
import me.domain.model.common.NuboBadge

// 사용자 인증 관련 인터페이스
interface NuboAuthRepository {
    suspend fun acknowledgeAchievements(token: String, keys: List<String>): NuboResponse<NuboResponseNothing>
    suspend fun checkEmail(email: String): NuboResponse<NuboResponseNothing>
    suspend fun checkName(name: String): NuboResponse<NuboResponseNothing>
    suspend fun clearUserInfo()
    suspend fun deleteAccount(token: String): NuboResponse<NuboResponseNothing>
    suspend fun getUserInfo(): NuboSigninResult
    suspend fun getUnannouncedAchievements(token: String): NuboResponse<List<NuboBadge>>
    suspend fun getSignupStatus(): NuboResponse<NuboSignupStatus>
    suspend fun logout(token: String): NuboResponse<NuboResponseNothing>
    suspend fun requestPasswordReset(email: String): NuboResponse<NuboResponseNothing>
    suspend fun signIn(id: String, password: String): NuboResponse<NuboSignin>
    suspend fun signInWithGoogle(idToken: String): NuboResponse<NuboSignin>
    suspend fun signUp(id: String, password: String, name: String, invite: String): NuboResponse<NuboSignup>
    suspend fun saveUserInfo(user: NuboSigninResult)
    suspend fun updateAccessToken(refresh: String): NuboResponse<NuboUpdateAccessToken>

    suspend fun updateUserInfo(param: NuboUpdateUserInfoParam): NuboResponse<NuboUpdateUserInfo>
    suspend fun verifyCode(param: NuboVerifyCodeParam): NuboResponse<NuboResponseNothing>
}

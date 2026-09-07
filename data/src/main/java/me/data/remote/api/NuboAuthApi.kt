package me.data.remote.api

import me.data.remote.dto.auth.DeleteAccountRequestDto
import me.data.remote.dto.auth.MobileRefreshRequestDto
import me.data.remote.dto.auth.PasswordResetRequestDto
import me.data.remote.dto.auth.SigninDto
import me.data.remote.dto.auth.SignupDto
import me.data.remote.dto.auth.SignupStatusResponseDto
import me.data.remote.dto.auth.UpdateAccessTokenDto
import me.data.remote.dto.auth.UpdateUserInfoDto
import me.data.remote.dto.common.BooleanResponseDto
import me.data.remote.dto.common.AchievementAcknowledgeRequestDto
import me.data.remote.dto.common.AchievementListResponseDto
import me.data.remote.dto.common.ResponseNothingDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part

interface NuboAuthApi {
    @GET("auth/signup/status")
    suspend fun getSignupStatus(): SignupStatusResponseDto

    @POST("auth/reset-password")
    suspend fun requestPasswordReset(
        @Body request: PasswordResetRequestDto
    ): ResponseNothingDto

    @GET("auth/user/achievements")
    suspend fun getUnannouncedAchievements(
        @Header("Authorization") authorization: String
    ): AchievementListResponseDto

    @PATCH("auth/user/achievements")
    suspend fun acknowledgeAchievements(
        @Header("Authorization") authorization: String,
        @Body request: AchievementAcknowledgeRequestDto
    ): ResponseNothingDto

    // 구글 로그인 후 id_token값 전송하고 토큰 받아오기
    @FormUrlEncoded
    @POST("auth/android/google")
    suspend fun signInWithGoogle(
        @Field("id_token") idToken: String
    ): SigninDto

    // 아이디가 존재하는지 확인하기
    @FormUrlEncoded
    @POST("auth/checkemail")
    suspend fun checkID(
        @Field("email") email: String
    ): BooleanResponseDto

    // 닉네임이 존재하는지 확인하기
    @FormUrlEncoded
    @POST("auth/checkname")
    suspend fun checkName(
        @Field("name") name: String
    ): BooleanResponseDto

    // 리프레시 토큰으로 새 액세스 토큰 발급 받기
    @POST("auth/android/refresh")
    suspend fun updateAccessToken(
        @Body request: MobileRefreshRequestDto
    ): UpdateAccessTokenDto

    // 로그인 하기
    @FormUrlEncoded
    @POST("auth/signin")
    suspend fun signIn(
        @Field("id") id: String,
        @Field("password") password: String
    ): SigninDto

    // 회원가입 하기
    @FormUrlEncoded
    @POST("auth/signup")
    suspend fun signUp(
        @Field("id") email: String,
        @Field("password") password: String,
        @Field("name") name: String,
        @Field("invite") invite: String
    ): SignupDto

    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") authorization: String
    ): ResponseNothingDto

    // 사용자의 정보 업데이트하기
    @Multipart
    @PATCH("auth/update")
    suspend fun updateUserInfo(
        @Header("Authorization") authorization: String,
        @Part("name") name: RequestBody,
        @Part("signature") signature: RequestBody,
        @Part("password") password: RequestBody,
        @Part profile: MultipartBody.Part?
    ): UpdateUserInfoDto

    // 회원가입 시 인증코드 확인하기
    @FormUrlEncoded
    @POST("auth/verify")
    suspend fun verifyCode(
        @Field("target") target: Int,
        @Field("code") code: String,
        @Field("id") email: String,
        @Field("password") password: String,
        @Field("name") name: String
    ): BooleanResponseDto

    // 계정과 계정에 연결된 모든 데이터를 영구 삭제하기
    @HTTP(method = "DELETE", path = "auth/account", hasBody = true)
    suspend fun deleteAccount(
        @Header("Authorization") authorization: String,
        @Body request: DeleteAccountRequestDto
    ): ResponseNothingDto
}

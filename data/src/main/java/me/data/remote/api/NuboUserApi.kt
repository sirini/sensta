package me.data.remote.api

import me.data.remote.dto.common.ResponseNothingDto
import me.data.remote.dto.user.ChatHistoryListResponseDto
import me.data.remote.dto.user.OtherUserInfoDto
import me.data.remote.dto.user.SendChatRequestDto
import me.data.remote.dto.user.SendChatResponseDto
import me.data.remote.dto.user.UserReportRequestDto
import me.data.remote.dto.user.UserSafetyStatusResponseDto
import me.data.remote.dto.user.UserTargetRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface NuboUserApi {
    // 상대방과 나눈 최근 메시지들 기록 가져오기
    @GET("chat/history")
    suspend fun getChatHistory(
        @Header("Authorization") authorization: String,
        @Query("targetUserUid") targetUserUid: Int,
        @Query("limit") limit: Int
    ): ChatHistoryListResponseDto

    // 상대방에게 메시지 보내기
    @POST("chat/save")
    suspend fun sendChatMessage(
        @Header("Authorization") authorization: String,
        @Body request: SendChatRequestDto
    ): SendChatResponseDto
    // 다른 사용자의 기본 정보 가져오기
    @GET("auth/user/info")
    suspend fun getOtherUserInfo(
        @Query("targetUserUid") targetUserUid: Int
    ): OtherUserInfoDto

    // 상대방 신고 및 차단 상태 확인하기
    @GET("auth/user/report")
    suspend fun getUserSafetyStatus(
        @Header("Authorization") authorization: String,
        @Query("targetUserUid") targetUserUid: Int
    ): UserSafetyStatusResponseDto

    // 사용자 또는 해당 사용자가 작성한 콘텐츠 신고하기
    @POST("auth/user/report")
    suspend fun reportUser(
        @Header("Authorization") authorization: String,
        @Body request: UserReportRequestDto
    ): ResponseNothingDto

    // 사용자 차단하기
    @retrofit2.http.PUT("auth/user/block")
    suspend fun blockUser(
        @Header("Authorization") authorization: String,
        @Body request: UserTargetRequestDto
    ): ResponseNothingDto

    // 사용자 차단 해제하기
    @HTTP(method = "DELETE", path = "auth/user/block", hasBody = true)
    suspend fun unblockUser(
        @Header("Authorization") authorization: String,
        @Body request: UserTargetRequestDto
    ): ResponseNothingDto
}

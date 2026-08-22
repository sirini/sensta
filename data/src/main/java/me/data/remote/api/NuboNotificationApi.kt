package me.data.remote.api

import me.data.remote.dto.common.ResponseNothingDto
import me.data.remote.dto.home.NotificationListResponseDto
import me.data.remote.dto.home.PushDeviceRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NuboNotificationApi {
    // 사용자에게 온 알림 내역 가져오기
    @GET("home/noti/load")
    suspend fun getUserNotifications(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int,
    ): NotificationListResponseDto

    // 사용자에게 온 개별 알림 내역 확인 처리하기
    @PATCH("home/noti/checked/{notiUid}")
    suspend fun checkNotification(
        @Header("Authorization") authorization: String,
        @Path("notiUid") notiUid: Int,
    ): ResponseNothingDto

    // 사용자에게 온 알림 내역 모두 확인 처리하기
    @PATCH("home/noti/checked")
    suspend fun checkAllNotifications(
        @Header("Authorization") authorization: String,
    ): ResponseNothingDto

    // 현재 기기의 Firebase 푸시 토큰 등록하기
    @POST("push/device")
    suspend fun registerPushDevice(
        @Header("Authorization") authorization: String,
        @Body request: PushDeviceRequestDto
    ): ResponseNothingDto

    // 로그아웃할 기기의 Firebase 푸시 토큰 해제하기
    @HTTP(method = "DELETE", path = "push/device", hasBody = true)
    suspend fun unregisterPushDevice(
        @Header("Authorization") authorization: String,
        @Body request: PushDeviceRequestDto
    ): ResponseNothingDto
}

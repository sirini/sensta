package me.data.repository

import me.data.remote.api.TsboardGoapi
import me.data.remote.dto.common.toEntity
import me.data.remote.dto.home.toEntity
import me.data.remote.dto.home.PushDeviceRequestDto
import me.domain.model.common.TsboardResponseNothing
import me.domain.model.home.TsboardNotification
import me.domain.repository.TsboardNotificationRepository
import me.domain.repository.TsboardResponse
import javax.inject.Inject

class TsboardNotificationRepositoryImpl @Inject constructor(
    private val api: TsboardGoapi
) : TsboardNotificationRepository {

    // 사용자에게 전달된 알림 내역 가져오기
    override suspend fun getUserNotifications(
        limit: Int,
        token: String
    ): TsboardResponse<List<TsboardNotification>> {
        return try {
            val response = api.getUserNotifications(
                authorization = "Bearer $token",
                limit = limit
            )
            TsboardResponse.Success(response.result?.map { it.toEntity() } ?: emptyList())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 개별 알림에 대해서 확인 처리하기
    override suspend fun checkNotification(
        notiUid: Int,
        token: String
    ): TsboardResponse<TsboardResponseNothing> {
        return try {
            val response = api.checkNotification(
                authorization = "Bearer $token",
                notiUid = notiUid
            )
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 사용자에게 온 전체 알림 확인 처리하기
    override suspend fun checkAllNotifications(token: String): TsboardResponse<TsboardResponseNothing> {
        return try {
            val response = api.checkAllNotifications(
                authorization = "Bearer $token"
            )
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 로그인한 사용자와 현재 기기의 푸시 토큰 연결하기
    override suspend fun registerPushDevice(
        deviceToken: String,
        token: String
    ): TsboardResponse<TsboardResponseNothing> {
        return try {
            val response = api.registerPushDevice(
                authorization = "Bearer $token",
                request = PushDeviceRequestDto(token = deviceToken, platform = "android")
            )
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "푸시 알림 기기를 등록하지 못했습니다")
        }
    }

    // 로그아웃 전에 현재 기기의 푸시 토큰 연결 해제하기
    override suspend fun unregisterPushDevice(
        deviceToken: String,
        token: String
    ): TsboardResponse<TsboardResponseNothing> {
        return try {
            val response = api.unregisterPushDevice(
                authorization = "Bearer $token",
                request = PushDeviceRequestDto(token = deviceToken, platform = "android")
            )
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "푸시 알림 기기를 해제하지 못했습니다")
        }
    }
}

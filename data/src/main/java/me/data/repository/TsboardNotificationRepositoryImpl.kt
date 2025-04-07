package me.data.repository

import me.data.remote.api.TsboardGoapi
import me.data.remote.dto.home.toEntity
import me.domain.model.home.TsboardNotification
import me.domain.repository.TsboardNotificationRepository
import me.domain.repository.TsboardResponse
import javax.inject.Inject

class TsboardNotificationRepositoryImpl @Inject constructor(
    private val api: TsboardGoapi
) : TsboardNotificationRepository {

    // 사용자에게 전달된 알림 내역 가져오기
    override suspend fun getUserNotifications(
        token: String,
        limit: Int
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
}
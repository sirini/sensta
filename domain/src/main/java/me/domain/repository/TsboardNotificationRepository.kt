package me.domain.repository

import me.domain.model.home.TsboardNotification

interface TsboardNotificationRepository {
    suspend fun getUserNotifications(
        token: String,
        limit: Int
    ): TsboardResponse<List<TsboardNotification>>
}
package me.domain.repository

import me.domain.model.common.ResponseNothing
import me.domain.model.home.TsboardNotification

interface TsboardNotificationRepository {
    suspend fun getUserNotifications(
        token: String,
        limit: Int
    ): TsboardResponse<List<TsboardNotification>>

    suspend fun checkNotification(token: String, notiUid: Int): TsboardResponse<ResponseNothing>
    suspend fun checkAllNotifications(token: String): TsboardResponse<ResponseNothing>
}
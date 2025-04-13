package me.domain.repository

import me.domain.model.common.TsboardResponseNothing
import me.domain.model.home.TsboardNotification

interface TsboardNotificationRepository {
    suspend fun getUserNotifications(
        limit: Int,
        token: String
    ): TsboardResponse<List<TsboardNotification>>

    suspend fun checkNotification(
        notiUid: Int,
        token: String
    ): TsboardResponse<TsboardResponseNothing>

    suspend fun checkAllNotifications(token: String): TsboardResponse<TsboardResponseNothing>
}
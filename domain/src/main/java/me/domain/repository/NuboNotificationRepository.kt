package me.domain.repository

import me.domain.model.common.NuboResponseNothing
import me.domain.model.home.NuboNotification

// 알림 관련 인터페이스
interface NuboNotificationRepository {
    suspend fun getUserNotifications(
        limit: Int,
        token: String
    ): NuboResponse<List<NuboNotification>>

    suspend fun checkNotification(
        notiUid: Int,
        token: String
    ): NuboResponse<NuboResponseNothing>

    suspend fun checkAllNotifications(token: String): NuboResponse<NuboResponseNothing>

    suspend fun registerPushDevice(
        deviceToken: String,
        token: String
    ): NuboResponse<NuboResponseNothing>

    suspend fun unregisterPushDevice(
        deviceToken: String,
        token: String
    ): NuboResponse<NuboResponseNothing>
}

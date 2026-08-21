package me.sensta.worker

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import me.domain.repository.handle
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.auth.SaveUserInfoUseCase
import me.domain.usecase.auth.UpdateAccessTokenUseCase
import me.domain.usecase.home.GetNotificationUseCase
import me.sensta.util.AppNotification

@HiltWorker
class NotificationCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val saveUserInfoUseCase: SaveUserInfoUseCase,
    private val getNotificationUseCase: GetNotificationUseCase,
    private val updateAccessTokenUseCase: UpdateAccessTokenUseCase,
) : CoroutineWorker(appContext, workerParams) {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val userInfo = getUserInfoUseCase().first()
        if (userInfo.token.isEmpty()) return Result.success()

        var accessToken = userInfo.token
        updateAccessTokenUseCase(refresh = userInfo.refresh).collect {
            it.handle { resp ->
                resp.result?.let { tokens ->
                    accessToken = tokens.token
                    saveUserInfoUseCase(
                        userInfo.copy(token = tokens.token, refresh = tokens.refresh)
                    )
                }
            }
        }

        getNotificationUseCase(accessToken, 20).collect { notifications ->
            AppNotification.check(applicationContext, notifications)
        }
        return Result.success()
    }
}

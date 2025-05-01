package me.sensta.worker

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.home.GetNotificationUseCase
import me.sensta.util.AppNotification

@HiltWorker
class NotificationCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private var getUserInfoUseCase: GetUserInfoUseCase,
    private var getNotificationUseCase: GetNotificationUseCase
) : CoroutineWorker(appContext, workerParams) {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        getUserInfoUseCase().collect { user ->
            if (user.token.isEmpty()) return@collect

            getNotificationUseCase(user.token, 20).collect { notifications ->
                AppNotification.check(applicationContext, notifications)
            }
        }
        return Result.success()
    }
}
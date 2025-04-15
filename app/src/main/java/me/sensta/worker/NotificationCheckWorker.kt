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
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.home.GetNotificationUseCase
import me.sensta.util.AppNotification

@HiltWorker
class NotificationCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getNotificationUseCase: GetNotificationUseCase
) : CoroutineWorker(appContext, workerParams) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val user = getUserInfoUseCase().first()
        if (user.token.isEmpty()) return Result.success()

        val noti = getNotificationUseCase(user.token, 20).first()
        AppNotification.check(applicationContext, noti)

        return Result.success()
    }
}
package me.sensta.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import me.sensta.ui.common.RequestNotificationPermission
import me.sensta.ui.navigation.AppNavigation
import me.sensta.ui.navigation.Screen
import me.sensta.ui.theme.SenstaTheme
import me.sensta.push.PushTokenManager
import me.sensta.push.PushDestinationResolver
import me.sensta.push.PushDestination
import me.sensta.push.PushEvent
import me.sensta.util.AppNotification
import me.sensta.worker.NotificationCheckWorker
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var pushTokenManager: PushTokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            SenstaTheme {
                RequestNotificationPermission(
                    onPermissionGranted = {
                        setupNotificationChannel()
                        configureNotificationFallback()
                    },
                    onPermissionDenied = {
                        Toast.makeText(this, "알림 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
                    }
                )

                val pushEvent = intent.toPushEvent()
                val startDestination = when {
                    pushEvent != null -> when (PushDestinationResolver.resolve(pushEvent)) {
                        PushDestination.Chat -> Screen.User.route
                        PushDestination.Post -> Screen.View.route
                        PushDestination.Notification -> Screen.Notification.route
                    }
                    intent.getStringExtra("navigate_to") == "notification" -> Screen.Notification.route
                    else -> Screen.Home.route
                }
                AppNavigation(startDestination = startDestination, initialPushEvent = pushEvent)
            }
        }
    }

    private fun setupNotificationChannel() {
        val channel = NotificationChannel(
            AppNotification.CHANNEL_ID,
            "활동 알림",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "좋아요, 댓글, 대화 등 계정 활동을 알려드립니다"
            setShowBadge(true)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun configureNotificationFallback() {
        val workManager = WorkManager.getInstance(this)
        if (pushTokenManager.isFirebaseConfigured()) {
            workManager.cancelUniqueWork(NOTIFICATION_WORK_NAME)
            return
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<NotificationCheckWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = java.util.concurrent.TimeUnit.MINUTES
        )
            .setConstraints(constraints).build()

        workManager.enqueueUniquePeriodicWork(
                NOTIFICATION_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }

    private companion object {
        const val NOTIFICATION_WORK_NAME = "sensta_notification_check"
    }

    private fun android.content.Intent.toPushEvent(): PushEvent? {
        val event = PushEvent(
            notificationType = getStringExtra("type")?.toIntOrNull(),
            postUid = getStringExtra("postUid")?.toIntOrNull() ?: 0,
            fromUserUid = getStringExtra("fromUserUid")?.toIntOrNull() ?: 0
        )
        return event.takeIf {
            it.notificationType != null || it.postUid > 0 || it.fromUserUid > 0
        }
    }
}

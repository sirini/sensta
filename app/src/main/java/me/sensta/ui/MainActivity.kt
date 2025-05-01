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
import me.sensta.worker.NotificationCheckWorker

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            SenstaTheme {
                RequestNotificationPermission(
                    onPermissionGranted = {
                        setupNotificationChannel()
                        setupPeriodicNotificationWorker()
                    },
                    onPermissionDenied = {
                        Toast.makeText(this, "알림 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
                    }
                )

                var startDestination = Screen.Home.route
                if (intent.getStringExtra("navigate_to") == "notification") {
                    startDestination = Screen.Notification.route
                }
                AppNavigation(startDestination)
            }
        }
    }

    private fun setupNotificationChannel() {
        val channel = NotificationChannel(
            "default",
            "Default Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "default notification channel"
            setShowBadge(true)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun setupPeriodicNotificationWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<NotificationCheckWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = java.util.concurrent.TimeUnit.MINUTES
        )
            .setConstraints(constraints).build()

        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                "sensta_notification_check",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }
}
package me.sensta.viewmodel.common

import androidx.compose.runtime.staticCompositionLocalOf
import me.sensta.viewmodel.NotificationViewModel

val LocalNotificationViewModel = staticCompositionLocalOf<NotificationViewModel> {
    error("No NotificationViewModel provided")
}
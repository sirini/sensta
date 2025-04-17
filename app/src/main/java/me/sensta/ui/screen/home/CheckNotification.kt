package me.sensta.ui.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import me.sensta.util.AppNotification
import me.sensta.viewmodel.local.LocalNotificationViewModel

@Composable
fun CheckNotification() {
    val context = LocalContext.current
    val notiViewModel = LocalNotificationViewModel.current
    val uncheckedNotification by notiViewModel.hasUncheckedNotification

    LaunchedEffect(Unit) {
        notiViewModel.loadNotifications()
        if (uncheckedNotification) {
            val user = notiViewModel.getUserInfo()
            if (user.token.isEmpty()) return@LaunchedEffect

            val noti = notiViewModel.getNotification(user.token, 20)
            AppNotification.check(context, noti)
        }
    }
}
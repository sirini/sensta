package me.sensta.ui.navigation.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController

@Composable
fun NotificationIcon(hasNotification: Boolean) {
    val navController = LocalNavController.current

    IconButton(onClick = {
        navController.navigate(Screen.Notification.route) {
            launchSingleTop = true
            restoreState = true
        }
    }) {
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            if (hasNotification) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "New notification",
                    tint = MaterialTheme.colorScheme.onBackground
                )

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color.Red, shape = CircleShape)
                        .border(
                            2.dp,
                            color = MaterialTheme.colorScheme.background,
                            shape = CircleShape
                        )
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = "No new notification",
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
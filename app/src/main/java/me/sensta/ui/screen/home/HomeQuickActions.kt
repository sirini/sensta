package me.sensta.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.data.env.Env
import me.domain.model.auth.hasCompleteSession
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.theme.LocalSenstaExtendedColors
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.local.LocalNotificationViewModel

@Composable
fun HomeQuickActions(modifier: Modifier = Modifier) {
    val navController = LocalNavController.current
    val authViewModel = LocalAuthViewModel.current
    val notificationViewModel = LocalNotificationViewModel.current
    val user by authViewModel.user
    val hasNotification by notificationViewModel.hasUncheckedNotification
    val onMedia = LocalSenstaExtendedColors.current.onMedia

    Row(
        modifier = modifier
            .statusBarsPadding()
            .padding(top = 8.dp, end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HomeQuickActionButton(
            contentDescription = if (user.hasCompleteSession) "내 정보" else "로그인",
            onClick = {
                navController.navigate(
                    if (user.hasCompleteSession) Screen.Profile.route else Screen.Login.route
                ) { launchSingleTop = true }
            }
        ) {
            if (user.hasCompleteSession && user.profile.isNotBlank()) {
                AsyncImage(
                    model = Env.DOMAIN + user.profile,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = if (user.hasCompleteSession) {
                        Icons.Default.AccountCircle
                    } else {
                        Icons.AutoMirrored.Default.Login
                    },
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = onMedia
                )
            }
        }

        if (user.hasCompleteSession) {
            HomeQuickActionButton(
                contentDescription = if (hasNotification) "읽지 않은 알림" else "알림",
                onClick = {
                    navController.navigate(Screen.Notification.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            ) {
                Icon(
                    imageVector = if (hasNotification) {
                        Icons.Default.NotificationsActive
                    } else {
                        Icons.Default.NotificationsNone
                    },
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = onMedia
                )
            }
        }
    }
}

@Composable
private fun HomeQuickActionButton(
    contentDescription: String,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .semantics { this.contentDescription = contentDescription },
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.18f),
        contentColor = LocalSenstaExtendedColors.current.onMedia
    ) {
        Box(contentAlignment = Alignment.Center) {
            content()
        }
    }
}

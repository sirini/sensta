package me.sensta.ui.navigation.topbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import me.data.env.Env
import me.sensta.ui.common.LocalScrollBehavior
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.theme.oleoScriptFontFamily
import me.sensta.viewmodel.local.LocalNotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    val navController = LocalNavController.current
    val notiViewModel = LocalNotificationViewModel.current
    val scrollBehavior = LocalScrollBehavior.current
    val hasNotification by notiViewModel.hasUncheckedNotification
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val showNotificationIcon = currentRoute != Screen.Login.route
    val sectionTitle = when (currentRoute) {
        Screen.Explorer.route -> "DISCOVER"
        Screen.Upload.route -> "PUBLISH"
        Screen.Profile.route -> "PROFILE"
        Screen.Notification.route -> "ACTIVITY"
        Screen.View.route -> "PHOTO STORY"
        Screen.User.route -> "PHOTOGRAPHER"
        Screen.UserMessage.route -> "MESSAGE"
        Screen.Version.route -> "ABOUT"
        else -> "CURATED PHOTOGRAPHY"
    }

    TopAppBar(
        title = {
            Column(
                modifier = Modifier.clickable {
                    navController.navigate(Screen.Home.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            ) {
                Text(
                    text = Env.TITLE.uppercase(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = oleoScriptFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp
                    )
                )
                Text(
                    text = sectionTitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        letterSpacing = 1.3.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.96f)
        ),
        actions = {
            if (showNotificationIcon) {
                NotificationIcon(hasNotification)
            }
        }
    )
}

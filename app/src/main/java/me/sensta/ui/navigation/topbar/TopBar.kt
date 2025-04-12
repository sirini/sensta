package me.sensta.ui.navigation.topbar

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import me.data.env.Env
import me.sensta.ui.common.LocalScrollBehavior
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.viewmodel.common.LocalNotificationViewModel

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

    TopAppBar(
        title = {
            Text(
                Env.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.clickable {
                    navController.navigate(Screen.Home.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                })
        },
        scrollBehavior = scrollBehavior,
        actions = {
            if (showNotificationIcon) {
                NotificationIcon(hasNotification)
            }
        }
    )
}
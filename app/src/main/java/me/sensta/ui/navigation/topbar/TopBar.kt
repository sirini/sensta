package me.sensta.ui.navigation.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import me.data.env.Env
import me.sensta.R
import me.sensta.ui.common.LocalScrollBehavior
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.viewmodel.local.LocalNotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val notiViewModel = LocalNotificationViewModel.current
    val scrollBehavior = LocalScrollBehavior.current
    val hasNotification by notiViewModel.hasUncheckedNotification
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val showNotificationIcon = currentRoute != Screen.Login.route

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = Env.TITLE,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.Home.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    })

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.large
                        )
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                        .clickable {
                            navController.navigate(Screen.Version.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                ) {
                    Text(
                        text = context.getString(R.string.version),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        actions = {
            if (showNotificationIcon) {
                NotificationIcon(hasNotification)
            }
        }
    )
}
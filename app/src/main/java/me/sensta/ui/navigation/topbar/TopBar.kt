package me.sensta.ui.navigation.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import me.data.env.Env
import me.sensta.R
import me.sensta.ui.common.LocalScrollBehavior
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.theme.robotoSlabFontFamily
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
        else -> "CURATED PHOTOGRAPHY"
    }

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                            fontFamily = robotoSlabFontFamily,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
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

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            shape = MaterialTheme.shapes.large
                        )
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                        .clickable {
                            navController.navigate(Screen.Version.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                ) {
                    Text(
                        text = "v${stringResource(R.string.version).removeSuffix("-debug")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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

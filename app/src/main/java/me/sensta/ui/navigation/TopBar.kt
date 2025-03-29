package me.sensta.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.data.env.Env
import me.sensta.ui.common.LocalScrollBehavior
import me.sensta.ui.navigation.common.LocalNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    val navController = LocalNavController.current
    val scrollBehavior = LocalScrollBehavior.current

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
            IconButton(onClick = {
                navController.navigate(Screen.Notification.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            }) {
                Icon(
                    imageVector = Screen.Notification.icon,
                    contentDescription = Screen.Notification.title
                )
            }
        }
    )
}
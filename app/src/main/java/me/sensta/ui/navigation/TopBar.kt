package me.sensta.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import me.data.env.Env
import me.sensta.ui.theme.titleFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController, scrollBehavior: TopAppBarScrollBehavior) {
    TopAppBar(
        title = { Text(Env.title, fontFamily = titleFontFamily) },
        scrollBehavior = scrollBehavior,
        actions = {
            IconButton(onClick = {
                navController.navigate(Screen.Notification.route) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                }
            }) {
                Icon(
                    imageVector = Screen.Notification.icon,
                    contentDescription = Screen.Notification.title
                )
            }

            IconButton(onClick = {
                navController.navigate(Screen.Config.route) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                }
            }) {
                Icon(imageVector = Screen.Config.icon, contentDescription = Screen.Config.title)
            }
        }
    )
}
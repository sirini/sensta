package me.sensta.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import me.sensta.ui.navigation.common.LocalNavController

@Composable
fun BottomNavigationBar() {
    val navController = LocalNavController.current
    val screens = listOf(Screen.Home, Screen.Explorer, Screen.Upload, Screen.Profile)
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    NavigationBar {
        screens.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = navBackStackEntry?.destination?.route == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}
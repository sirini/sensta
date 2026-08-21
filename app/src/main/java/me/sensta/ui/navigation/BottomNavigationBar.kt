package me.sensta.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import me.data.env.Env
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.viewmodel.local.LocalAuthViewModel

@Composable
fun BottomNavigationBar() {
    val navController = LocalNavController.current
    val screens = listOf(Screen.Home, Screen.Explorer, Screen.Upload)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val authViewModel = LocalAuthViewModel.current
    val user by authViewModel.user

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
            )
            NavigationBar(
                modifier = Modifier.height(64.dp),
                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                tonalElevation = 0.dp
            ) {
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = navBackStackEntry?.destination?.route == screen.route,
                        onClick = {
                            if (navBackStackEntry?.destination?.route != screen.route) {
                                navController.navigate(screen.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }

                NavigationBarItem(
                    icon = {
                        if (user.profile.isEmpty()) {
                            Icon(
                                Screen.Profile.icon,
                                contentDescription = Screen.Profile.title,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            AsyncImage(
                                model = Env.DOMAIN + user.profile,
                                contentDescription = user.name,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                            )
                        }
                    },
                    label = { Text(Screen.Profile.title) },
                    selected = navBackStackEntry?.destination?.route == Screen.Profile.route,
                    onClick = {
                        navController.navigate(Screen.Profile.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

package me.sensta.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.sensta.ui.screen.ConfigScreen
import me.sensta.ui.screen.ExplorerScreen
import me.sensta.ui.screen.HomeScreen
import me.sensta.ui.screen.NotificationScreen
import me.sensta.ui.screen.PostScreen
import me.sensta.ui.screen.ProfileScreen
import me.sensta.ui.screen.UploadScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "홈", Icons.Default.Home)
    data object Post : Screen("post", "게시글", Icons.Default.Info)
    data object Explorer : Screen("explorer", "탐색", Icons.Default.Search)
    data object Upload : Screen("upload", "업로드", Icons.Default.Add)
    data object Profile : Screen("profile", "내정보", Icons.Default.AccountCircle)
    data object Notification : Screen("notification", "알림", Icons.Default.Notifications)
    data object Config : Screen("config", "설정", Icons.Default.Settings)
}

// 전역에서 사용 가능한 NavController
val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No NavController provided")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    CompositionLocalProvider(LocalNavController provides navController) {
        Scaffold(
            topBar = { TopBar(navController, scrollBehavior) },
            bottomBar = { BottomNavigationBar(navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier
                    .padding(innerPadding)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                composable(Screen.Home.route) { HomeScreen() }
                composable(Screen.Post.route) { PostScreen() }
                composable(Screen.Explorer.route) { ExplorerScreen() }
                composable(Screen.Upload.route) { UploadScreen() }
                composable(Screen.Profile.route) { ProfileScreen() }
                composable(Screen.Notification.route) { NotificationScreen() }
                composable(Screen.Config.route) { ConfigScreen() }
            }
        }
    }
}
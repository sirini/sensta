package me.sensta.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.sensta.ui.common.LocalScrollBehavior
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.navigation.topbar.TopBar
import me.sensta.ui.screen.ConfigScreen
import me.sensta.ui.screen.ExplorerScreen
import me.sensta.ui.screen.HomeScreen
import me.sensta.ui.screen.LoginScreen
import me.sensta.ui.screen.NotificationScreen
import me.sensta.ui.screen.ProfileScreen
import me.sensta.ui.screen.SignupScreen
import me.sensta.ui.screen.UploadScreen
import me.sensta.ui.screen.VersionScreen
import me.sensta.ui.screen.ViewScreen
import me.sensta.ui.screen.home.post.PostCardFullScreen
import me.sensta.ui.screen.view.ViewPostCommentDialog
import me.sensta.viewmodel.AuthViewModel
import me.sensta.viewmodel.CommonViewModel
import me.sensta.viewmodel.HomeViewModel
import me.sensta.viewmodel.NotificationViewModel
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.local.LocalCommonViewModel
import me.sensta.viewmodel.local.LocalHomeViewModel
import me.sensta.viewmodel.local.LocalNotificationViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Config : Screen("config", "설정", Icons.Default.Settings)
    data object Explorer : Screen("explorer", "탐색", Icons.Default.Search)
    data object Home : Screen("home", "홈", Icons.Default.Home)
    data object Login : Screen("login", "로그인", Icons.AutoMirrored.Default.Login)
    data object Notification : Screen("notification", "알림", Icons.Default.Notifications)
    data object Profile : Screen("profile", "내정보", Icons.Default.AccountCircle)
    data object Signup : Screen("signup", "회원가입", Icons.Default.GroupAdd)
    data object Upload : Screen("upload", "업로드", Icons.Default.Upload)
    data object View : Screen("view", "게시글", Icons.AutoMirrored.Default.Article)
    data object Version : Screen("version", "버전", Icons.Default.Verified)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val commonViewModel: CommonViewModel = hiltViewModel()
    val notiViewModel: NotificationViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val showFullScreen by commonViewModel.showFullScreen
    val showCommentDialog by commonViewModel.showCommentDialog

    CompositionLocalProvider(
        LocalCommonViewModel provides commonViewModel,
        LocalHomeViewModel provides homeViewModel,
        LocalNotificationViewModel provides notiViewModel,
        LocalAuthViewModel provides authViewModel,
        LocalNavController provides navController,
        LocalScrollBehavior provides scrollBehavior,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Scaffold(
                topBar = { TopBar() },
                bottomBar = { BottomNavigationBar() }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier
                        .padding(innerPadding)
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    composable(Screen.Config.route) { ConfigScreen() }
                    composable(Screen.Explorer.route) { ExplorerScreen() }
                    composable(Screen.Home.route) { HomeScreen() }
                    composable(Screen.Login.route) { LoginScreen() }
                    composable(Screen.Notification.route) { NotificationScreen() }
                    composable(Screen.Profile.route) { ProfileScreen() }
                    composable(Screen.Signup.route) { SignupScreen() }
                    composable(Screen.Upload.route) { UploadScreen() }
                    composable(Screen.View.route) { ViewScreen() }
                    composable(Screen.Version.route) { VersionScreen() }
                }

                // 댓글 작성하기 다이얼로그
                if (showCommentDialog) {
                    ViewPostCommentDialog(
                        onDismissRequest = { commonViewModel.closeWriteCommentDialog() }
                    ) {
                        // TODO 댓글 작성 처리
                        commonViewModel.closeWriteCommentDialog()
                    }
                }
            }

            // 전체 화면 오버레이
            AnimatedVisibility(
                visible = showFullScreen,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
            ) {
                PostCardFullScreen()
            }
        }
    }
}
package me.sensta.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import me.domain.repository.TsboardResponse
import me.sensta.ui.common.LocalScrollBehavior
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.screen.notification.NotificationList
import me.sensta.viewmodel.AuthViewModel
import me.sensta.viewmodel.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val scrollBehavior = LocalScrollBehavior.current
    val user by authViewModel.user.collectAsState()

    // 스크롤 상태를 초기화해서 topBar가 펼쳐진 상태로 만들기
    LaunchedEffect(Unit) {
        scrollBehavior.state.heightOffset = 0f
        authViewModel.refresh(context)
        if (user.token.isEmpty()) {
            navController.navigate(Screen.Login.route) {
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    when (val notificationResponse = notificationViewModel.notifications) {
        is TsboardResponse.Loading -> {
            LoadingScreen()
        }

        is TsboardResponse.Success -> {
            NotificationList(notificationResponse.data)
        }

        is TsboardResponse.Error -> {
            ErrorScreen()
        }
    }
}
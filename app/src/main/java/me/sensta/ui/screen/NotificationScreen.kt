package me.sensta.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import me.domain.repository.TsboardResponse
import me.sensta.ui.common.LocalScrollBehavior
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.screen.notification.NotificationList
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.local.LocalNotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen() {
    val navController = LocalNavController.current
    val notiViewModel = LocalNotificationViewModel.current
    val authViewModel = LocalAuthViewModel.current
    val scrollBehavior = LocalScrollBehavior.current
    val isLoading by authViewModel.isLoading
    val user by authViewModel.user.collectAsState()
    val notis by notiViewModel.notifications

    // 스크롤 상태를 초기화해서 topBar가 펼쳐진 상태로 만들기
    LaunchedEffect(Unit) {
        scrollBehavior.state.heightOffset = 0f
        authViewModel.refresh()
    }

    // user 정보를 다 블러오면 로그인 여부를 체크
    if (!isLoading && user.uid < 1) {
        LaunchedEffect(Unit) {
            navController.navigate(Screen.Login.route) {
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    when (val notificationResponse = notis) {
        is TsboardResponse.Loading -> LoadingScreen()
        is TsboardResponse.Success -> NotificationList(notificationResponse.data)
        is TsboardResponse.Error -> ErrorScreen()
    }
}
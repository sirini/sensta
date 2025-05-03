package me.sensta.ui.screen

import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import me.domain.repository.TsboardResponse
import me.sensta.ui.common.LocalScrollBehavior
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.screen.notification.NotificationList
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.local.LocalNotificationViewModel
import me.sensta.viewmodel.uievent.NotificationUiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val notiViewModel = LocalNotificationViewModel.current
    val authViewModel = LocalAuthViewModel.current
    val scrollBehavior = LocalScrollBehavior.current
    val isLoading by authViewModel.isLoading
    val user by authViewModel.user.collectAsState()
    val notis by notiViewModel.notifications

    LaunchedEffect(Unit) {
        // 스크롤 상태를 초기화해서 topBar가 펼쳐진 상태로 만들기
        scrollBehavior.state.heightOffset = 0f
        authViewModel.refresh()

        // ViewModel로부터 이벤트 수신해서 메시지 보여주기
        notiViewModel.uiEvent.collect { event ->
            when (event) {
                is NotificationUiEvent.NotificationListUpdated -> {
                    Toast.makeText(context, "알림 목록을 업데이트 하였습니다", Toast.LENGTH_SHORT).show()
                }

                is NotificationUiEvent.AllNotificationsChecked -> {
                    Toast.makeText(context, "모든 알림을 확인하였습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
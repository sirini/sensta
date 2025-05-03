package me.sensta.ui.screen

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import me.domain.repository.TsboardResponse
import me.sensta.ui.screen.home.PhotoError
import me.sensta.ui.screen.home.PhotoList
import me.sensta.util.AppNotification
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.local.LocalHomeViewModel
import me.sensta.viewmodel.local.LocalNotificationViewModel
import me.sensta.viewmodel.uievent.HomeUiEvent

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val authViewModel = LocalAuthViewModel.current
    val homeViewModel = LocalHomeViewModel.current
    val notiViewModel = LocalNotificationViewModel.current
    val user by authViewModel.user.collectAsState()
    val uncheckedNotification by notiViewModel.hasUncheckedNotification
    val photos by homeViewModel.photos

    LaunchedEffect(Unit) {
        // 사용자 정보 업데이트하기
        authViewModel.refresh()

        // HomeViewModel에서 전달된 이벤트들에 따라 메시지 출력하기
        homeViewModel.uiEvent.collect { event ->
            when (event) {
                is HomeUiEvent.LikePost -> {
                    Toast.makeText(context, "게시글에 좋아요를 남겼습니다.", Toast.LENGTH_SHORT).show()
                }

                is HomeUiEvent.CancelLikePost -> {
                    Toast.makeText(context, "좋아요를 취소했습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(user) {
        homeViewModel.refresh(true)

        if (user.token.isNotEmpty()) {
            notiViewModel.loadNotifications()
            if (uncheckedNotification) {
                val notifications = notiViewModel.getNotification(user.token, 20)
                AppNotification.check(context, notifications)
            }
        }
    }

    // 사진 목록 가져오기
    when (val photoResponse = photos) {
        is TsboardResponse.Loading -> LoadingScreen()
        is TsboardResponse.Success -> PhotoList(photoResponse.data)
        is TsboardResponse.Error -> PhotoError(viewModel = homeViewModel)
    }
}
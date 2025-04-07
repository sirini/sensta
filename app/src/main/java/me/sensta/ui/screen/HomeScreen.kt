package me.sensta.ui.screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import me.domain.repository.TsboardResponse
import me.sensta.ui.screen.home.PhotoError
import me.sensta.ui.screen.home.PhotoList
import me.sensta.viewmodel.HomeViewModel
import me.sensta.viewmodel.NotificationViewModel

@Composable
fun HomeScreen() {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val notificationViewModel: NotificationViewModel = hiltViewModel()

    // 사진 목록 가져오기
    when (val photoResponse = homeViewModel.photos) {
        is TsboardResponse.Loading -> {
            LoadingScreen()
        }

        is TsboardResponse.Success -> {
            PhotoList(photoResponse.data)
        }

        is TsboardResponse.Error -> {
            PhotoError(viewModel = homeViewModel)
        }
    }

    // 알림 목록 가져오기
    when (val notificationResponse = notificationViewModel.notifications) {
        is TsboardResponse.Loading -> {}
        
        is TsboardResponse.Success -> {
            notificationViewModel.updateNotificationCount(notificationResponse.data.size)
        }

        is TsboardResponse.Error -> {}
    }
}
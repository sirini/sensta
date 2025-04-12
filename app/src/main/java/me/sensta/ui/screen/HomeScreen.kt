package me.sensta.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import me.domain.repository.TsboardResponse
import me.sensta.ui.screen.home.PhotoError
import me.sensta.ui.screen.home.PhotoList
import me.sensta.viewmodel.HomeViewModel
import me.sensta.viewmodel.common.LocalNotificationViewModel

@Composable
fun HomeScreen() {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val notiViewModel = LocalNotificationViewModel.current
    val photos by homeViewModel.photos

    LaunchedEffect(Unit) {
        notiViewModel.loadNotifications()
    }

    // 사진 목록 가져오기
    when (val photoResponse = photos) {
        is TsboardResponse.Loading -> LoadingScreen()
        is TsboardResponse.Success -> PhotoList(photoResponse.data)
        is TsboardResponse.Error -> PhotoError(viewModel = homeViewModel)
    }
}
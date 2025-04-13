package me.sensta.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import me.domain.repository.TsboardResponse
import me.sensta.ui.screen.home.CheckNotification
import me.sensta.ui.screen.home.PhotoError
import me.sensta.ui.screen.home.PhotoList
import me.sensta.viewmodel.common.LocalHomeViewModel

@Composable
fun HomeScreen() {
    val homeViewModel = LocalHomeViewModel.current
    val photos by homeViewModel.photos

    CheckNotification()

    // 사진 목록 가져오기
    when (val photoResponse = photos) {
        is TsboardResponse.Loading -> LoadingScreen()
        is TsboardResponse.Success -> PhotoList(photoResponse.data)
        is TsboardResponse.Error -> PhotoError(viewModel = homeViewModel)
    }
}
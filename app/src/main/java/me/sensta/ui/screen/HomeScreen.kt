package me.sensta.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import me.domain.repository.TsboardResponse
import me.sensta.ui.screen.home.Loading
import me.sensta.ui.screen.home.PhotoError
import me.sensta.ui.screen.home.PhotoList
import me.sensta.viewmodel.PhotoViewModel

@Composable
fun HomeScreen() {
    val viewModel: PhotoViewModel = hiltViewModel()
    val photoResponse by viewModel.photos.collectAsState()

    when (photoResponse) {
        is TsboardResponse.Loading -> {
            Loading()
        }

        is TsboardResponse.Success -> {
            PhotoList(photoResponse = photoResponse)
        }

        is TsboardResponse.Error -> {
            PhotoError(viewModel = viewModel)
        }
    }
}
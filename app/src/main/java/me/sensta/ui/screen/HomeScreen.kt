package me.sensta.ui.screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import me.domain.repository.TsboardResponse
import me.sensta.ui.screen.home.Loading
import me.sensta.ui.screen.home.PhotoError
import me.sensta.ui.screen.home.PhotoList
import me.sensta.viewmodel.HomeViewModel

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = hiltViewModel()

    when (val photoResponse = viewModel.photos) {
        is TsboardResponse.Loading -> {
            Loading()
        }

        is TsboardResponse.Success -> {
            PhotoList(photoResponse.data)
        }

        is TsboardResponse.Error -> {
            PhotoError(viewModel = viewModel)
        }
    }
}
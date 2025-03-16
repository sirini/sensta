package me.sensta.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import me.domain.repository.TsboardResponse
import me.sensta.ui.screen.home.Loading
import me.sensta.ui.screen.home.PhotoError
import me.sensta.ui.screen.home.PhotoList
import me.sensta.viewmodel.PhotoListViewModel

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val viewModel: PhotoListViewModel = hiltViewModel()
    val photoResponse by viewModel.photos.collectAsState()

    when (photoResponse) {
        is TsboardResponse.Loading -> {
            Loading(modifier = modifier)
        }

        is TsboardResponse.Success -> {
            PhotoList(photoResponse = photoResponse)
        }

        is TsboardResponse.Error -> {
            PhotoError(
                viewModel = viewModel,
                modifier = modifier
            )
        }
    }
}
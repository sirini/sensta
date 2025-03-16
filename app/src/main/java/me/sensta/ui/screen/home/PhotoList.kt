package me.sensta.ui.screen.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.domain.model.gallery.TsboardPhoto
import me.domain.repository.TsboardResponse
import me.sensta.ui.screen.home.card.PostCard

@Composable
fun PhotoList(
    photoResponse: TsboardResponse<List<TsboardPhoto>>
) {
    val photos = (photoResponse as TsboardResponse.Success<List<TsboardPhoto>>).data

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(photos) { photo ->
            PostCard(photo = photo)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
package me.sensta.ui.screen.home.card

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import me.data.env.Env
import me.domain.model.gallery.TsboardPhoto

@Composable
fun PostCard(photo: TsboardPhoto) {
    PostCardHeader(photo)
    AsyncImage(
        model = Env.domain + photo.images[0].thumbnail.large,
        contentDescription = photo.title,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentScale = ContentScale.Crop
    )
    PostCardFooter(photo)
}
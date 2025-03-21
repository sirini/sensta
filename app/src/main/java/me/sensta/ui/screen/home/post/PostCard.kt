package me.sensta.ui.screen.home.post

import androidx.compose.runtime.Composable
import me.domain.model.photo.TsboardPhoto

@Composable
fun PostCard(photo: TsboardPhoto) {
    PostCardHeader(photo)
    PostCarousel(photo.images)
    PostCardFooter(photo)
}
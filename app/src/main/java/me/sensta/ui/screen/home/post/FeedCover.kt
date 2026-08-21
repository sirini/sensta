package me.sensta.ui.screen.home.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import me.data.env.Env
import me.sensta.ui.theme.LocalSenstaExtendedColors

@Composable
fun FeedCover(path: String, title: String, onClick: () -> Unit) {
    val mediaColor = LocalSenstaExtendedColors.current.media

    AsyncImage(
        model = Env.DOMAIN + path,
        contentDescription = title,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .background(mediaColor)
            .clickable(onClick = onClick),
        contentScale = ContentScale.Crop
    )
}

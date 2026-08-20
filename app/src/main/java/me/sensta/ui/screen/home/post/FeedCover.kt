package me.sensta.ui.screen.home.post

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import me.data.env.Env
import me.sensta.viewmodel.local.LocalCommonViewModel
import me.sensta.ui.theme.LocalSenstaExtendedColors

@Composable
fun FeedCover(path: String, title: String) {
    val commonViewModel = LocalCommonViewModel.current
    val mediaColor = LocalSenstaExtendedColors.current.media

    AsyncImage(
        model = Env.DOMAIN + path,
        contentDescription = title,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .background(mediaColor)
            .pointerInput(path) {
                detectTapGestures { commonViewModel.openFullScreen(path) }
            },
        contentScale = ContentScale.Crop
    )
}

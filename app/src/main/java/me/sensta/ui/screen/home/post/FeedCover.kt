package me.sensta.ui.screen.home.post

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import me.data.env.Env
import me.sensta.viewmodel.local.LocalCommonViewModel

@Composable
fun FeedCover(path: String, title: String) {
    val commonViewModel = LocalCommonViewModel.current

    AsyncImage(
        model = Env.DOMAIN + path,
        contentDescription = title,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .pointerInput(path) {
                detectTapGestures { commonViewModel.openFullScreen(path) }
            },
        contentScale = ContentScale.Crop
    )
}

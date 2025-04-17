package me.sensta.ui.screen.home.post

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import me.data.env.Env
import me.sensta.viewmodel.local.LocalCommonViewModel
import kotlin.math.abs

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PostCardFullScreen() {
    val commonViewModel = LocalCommonViewModel.current
    val fullImagePath by commonViewModel.fullImagePath

    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .zIndex(1f)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 5f)
                    offsetX += pan.x
                    offsetY += pan.y

                    if (abs(offsetY) > 300f && scale < 1.2f) {
                        commonViewModel.closeFullScreen()
                    }
                }
            }
    ) {
        AsyncImage(
            model = Env.domain + fullImagePath,
            contentDescription = "uploaded image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                )
        )

        IconButton(
            onClick = { commonViewModel.closeFullScreen() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 32.dp, end = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "close",
                tint = Color.White
            )
        }
    }
}
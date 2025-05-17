package me.sensta.ui.screen.home.post

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import me.data.env.Env
import me.sensta.viewmodel.local.LocalCommonViewModel
import kotlin.math.max

@Composable
fun PostCardFullScreen() {
    val context = LocalContext.current
    val density = LocalDensity.current
    val commonViewModel = LocalCommonViewModel.current
    val fullImagePath by commonViewModel.fullImagePath
    val screenWidth = remember {
        with(density) { context.resources.displayMetrics.widthPixels }
    }
    val screenHeight = remember {
        with(density) { context.resources.displayMetrics.heightPixels }
    }

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
                    if (zoom < 0.9f) {
                        commonViewModel.closeFullScreen()
                        return@detectTransformGestures
                    }
                    scale = (scale * zoom).coerceIn(1f, 5f)

                    val imageWidth = screenWidth * scale
                    val imageHeight = screenHeight * scale * 0.6f

                    val maxOffsetX = max((imageWidth - screenWidth) / 2f, 0f)
                    val maxOffsetY = max((imageHeight - screenHeight) / 2f, 0f)

                    offsetX = (offsetX + pan.x).coerceIn(-maxOffsetX, maxOffsetX)
                    offsetY = (offsetY + pan.y).coerceIn(-maxOffsetY, maxOffsetY)

                    if (pan.y > 180f || pan.y < -180f) {
                        commonViewModel.closeFullScreen()
                    }
                }
            }
    ) {
        AsyncImage(
            model = Env.DOMAIN + fullImagePath,
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
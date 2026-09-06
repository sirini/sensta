package me.sensta.ui.screen.home.post

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import me.data.env.Env
import me.sensta.ui.theme.LocalSenstaExtendedColors

@Composable
fun FeedCover(
    path: String,
    title: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mediaColor = LocalSenstaExtendedColors.current.media

    AsyncImage(
        model = Env.DOMAIN + path,
        contentDescription = title,
        modifier = modifier
            .fillMaxSize()
            .background(mediaColor)
            .combinedClickable(
                onClickLabel = "게시글 상세 보기",
                onLongClickLabel = "배경화면으로 설정",
                onClick = onClick,
                onLongClick = onLongClick
            ),
        contentScale = ContentScale.Crop
    )
}

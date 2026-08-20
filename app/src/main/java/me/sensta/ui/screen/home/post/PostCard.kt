package me.sensta.ui.screen.home.post

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import me.domain.model.board.TsboardPost

@Composable
fun PostCard(post: TsboardPost) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
        tonalElevation = 1.dp
    ) {
        Column {
            PostCardHeader(writer = post.writer)
            FeedCover(path = post.cover, title = post.title)
            PostCardFooter(post)
        }
    }
}

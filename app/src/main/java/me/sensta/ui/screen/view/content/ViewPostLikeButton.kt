package me.sensta.ui.screen.view.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.domain.model.board.TsboardPost

@Composable
fun ViewPostLikeButton(post: TsboardPost) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = {/* TODO */ }) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = "like",
                modifier = Modifier.size(40.dp)
            )
        }
        Text(text = "${post.like}개 좋아요", style = MaterialTheme.typography.bodySmall)
    }
}
package me.sensta.ui.screen.view.comment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.data.env.Env
import me.domain.model.board.TsboardComment

@Composable
fun CommentCardHeader(comment: TsboardComment, likeState: Boolean, onLikeClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (comment.writer.profile.isNotEmpty()) {
                AsyncImage(
                    model = Env.domain + comment.writer.profile,
                    contentDescription = comment.writer.name,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "unknown profile",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = comment.writer.name,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onLikeClick) {
                if (likeState) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "like",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "like"
                    )
                }
            }
        }
    }
}
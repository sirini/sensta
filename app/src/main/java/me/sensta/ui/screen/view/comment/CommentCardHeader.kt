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
import androidx.compose.material.icons.automirrored.filled.Reply
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
import me.domain.model.board.NuboComment

@Composable
fun CommentCardHeader(
    comment: NuboComment,
    likeState: Boolean,
    isReply: Boolean,
    onLikeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isReply) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.Reply,
                    contentDescription = "답글",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            if (comment.writer.profile.isNotEmpty()) {
                AsyncImage(
                    model = Env.DOMAIN + comment.writer.profile,
                    contentDescription = comment.writer.name,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                        contentDescription = "기본 프로필",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = comment.writer.name,
                style = MaterialTheme.typography.titleSmall
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onLikeClick) {
                if (likeState) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "좋아요 취소",
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "좋아요"
                    )
                }
            }
        }
    }
}

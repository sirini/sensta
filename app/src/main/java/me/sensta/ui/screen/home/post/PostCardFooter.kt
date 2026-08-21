package me.sensta.ui.screen.home.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.data.env.Env
import me.domain.model.board.TsboardPost
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.theme.LocalSenstaExtendedColors
import me.sensta.viewmodel.local.LocalCommonViewModel
import me.sensta.viewmodel.local.LocalHomeViewModel
import me.sensta.viewmodel.local.LocalUserChatViewModel

@Composable
fun PostCardFooter(
    post: TsboardPost,
    onViewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = LocalNavController.current
    val commonViewModel = LocalCommonViewModel.current
    val homeViewModel = LocalHomeViewModel.current
    val userViewModel = LocalUserChatViewModel.current
    val onMedia = LocalSenstaExtendedColors.current.onMedia

    var likeState by remember(post.uid, post.liked) { mutableStateOf(post.liked) }
    var likeCount by remember(post.uid, post.like) { mutableIntStateOf(post.like) }

    val doLike: () -> Unit = {
        likeState = !likeState
        homeViewModel.like(post.uid, likeState)
        likeCount += if (likeState) 1 else -1
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    userViewModel.loadOtherUserInfo(post.writer)
                    navController.navigate(Screen.User.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = Env.DOMAIN + post.writer.profile,
                contentDescription = post.writer.name,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = post.writer.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = onMedia
                )
                Text(
                    text = "PHOTOGRAPHER",
                    style = MaterialTheme.typography.labelSmall,
                    color = onMedia.copy(alpha = 0.72f)
                )
            }
        }

        Text(
            text = post.title,
            style = MaterialTheme.typography.titleLarge,
            color = onMedia,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp, bottom = 4.dp)
                .clickable(onClick = onViewClick)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = doLike, modifier = Modifier.size(42.dp)) {
                    Icon(
                        imageVector = if (likeState) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (likeState) "좋아요 취소" else "좋아요",
                        modifier = Modifier.size(22.dp),
                        tint = if (likeState) MaterialTheme.colorScheme.primary else onMedia
                    )
                }
                Text(
                    text = likeCount.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = onMedia
                )
                IconButton(
                    onClick = { commonViewModel.openWriteCommentDialog(post.uid) },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "댓글 쓰기",
                        modifier = Modifier.size(22.dp),
                        tint = onMedia
                    )
                }
                Text(
                    text = post.comment.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = onMedia
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = onMedia.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = post.hit.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = onMedia.copy(alpha = 0.8f)
                )
            }
        }
    }
}

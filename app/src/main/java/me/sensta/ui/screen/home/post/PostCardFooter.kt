package me.sensta.ui.screen.home.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.data.env.Env
import me.domain.model.board.NuboPost
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.theme.LocalSenstaExtendedColors
import me.sensta.util.sharePost
import me.sensta.viewmodel.local.LocalCommonViewModel
import me.sensta.viewmodel.local.LocalHomeViewModel
import me.sensta.viewmodel.local.LocalUserChatViewModel

@Composable
fun PostCardFooter(
    post: NuboPost,
    onViewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val commonViewModel = LocalCommonViewModel.current
    val homeViewModel = LocalHomeViewModel.current
    val userViewModel = LocalUserChatViewModel.current
    val onMedia = LocalSenstaExtendedColors.current.onMedia

    val doLike: () -> Unit = {
        homeViewModel.like(post.uid, !post.liked, post.like)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
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
            if (post.writer.profile.isNotBlank()) {
                AsyncImage(
                    model = Env.DOMAIN + post.writer.profile,
                    contentDescription = post.writer.name,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Column {
                WriterName(
                    writer = post.writer,
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
                .padding(top = 8.dp, bottom = 12.dp)
                .clickable(onClick = onViewClick)
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = doLike, modifier = Modifier.size(42.dp)) {
                    Icon(
                        imageVector = if (post.liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (post.liked) "좋아요 취소" else "좋아요",
                        modifier = Modifier.size(22.dp),
                        tint = if (post.liked) MaterialTheme.colorScheme.primary else onMedia
                    )
                }
                Text(
                    text = post.like.toString(),
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

            FilledIconButton(
                onClick = {
                    navController.navigate(Screen.Upload.route) {
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "사진 올리기",
                    modifier = Modifier.size(26.dp)
                )
            }

            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.navigate(Screen.Explorer.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "탐색",
                        modifier = Modifier.size(20.dp),
                        tint = onMedia
                    )
                }
                IconButton(
                    onClick = { sharePost(context, post.uid, post.title) },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "게시물 공유",
                        modifier = Modifier.size(20.dp),
                        tint = onMedia
                    )
                }
            }
        }
    }
}

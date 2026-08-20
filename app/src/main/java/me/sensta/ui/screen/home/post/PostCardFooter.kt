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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.domain.model.board.TsboardPost
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.viewmodel.local.LocalCommonViewModel
import me.sensta.viewmodel.local.LocalHomeViewModel

@Composable
fun PostCardFooter(post: TsboardPost) {
    val navController = LocalNavController.current
    val commonViewModel = LocalCommonViewModel.current
    val homeViewModel = LocalHomeViewModel.current

    var likeState by remember(post.uid, post.liked) { mutableStateOf(post.liked) }
    var likeCount by remember(post.uid, post.like) { mutableIntStateOf(post.like) }
    val commentCount = post.comment

    // 좋아요 클릭
    val doLike: () -> Unit = {
        likeState = !likeState
        homeViewModel.like(post.uid, likeState)

        if (likeState) {
            likeCount++
        } else {
            likeCount--
        }
    }

    // 게시글 보기 페이지로 이동
    val moveToView: () -> Unit = {
        commonViewModel.updatePostUid(post.uid)
        navController.navigate(Screen.View.route) {
            launchSingleTop = true
            restoreState = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = doLike) {
                    if (likeState) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "좋아요 취소",
                            modifier = Modifier.size(22.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "좋아요",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                IconButton(onClick = { commonViewModel.openWriteCommentDialog(post.uid) }) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "댓글 쓰기",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            TextButton(onClick = moveToView) {
                Text(text = "사진 보기", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Text(
            text = post.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .clickable(onClick = moveToView)
        )
        Text(
            text = "좋아요 ${likeCount} · 댓글 ${commentCount} · 조회 ${post.hit}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
        )
    }
}

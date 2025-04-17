package me.sensta.ui.screen.home.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.domain.model.photo.TsboardPhoto
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.viewmodel.local.LocalCommonViewModel
import me.sensta.viewmodel.local.LocalHomeViewModel

@Composable
fun PostCardFooter(photo: TsboardPhoto) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val commonViewModel = LocalCommonViewModel.current
    val homeViewModel = LocalHomeViewModel.current

    var likeState by remember { mutableStateOf(photo.liked) }
    var likeCount by remember { mutableIntStateOf(photo.like) }
    var commentCount by remember { mutableIntStateOf(photo.comment) }

    // 좋아요 클릭
    val doLike: () -> Unit = {
        likeState = !likeState
        homeViewModel.like(photo.uid, likeState, context)

        if (likeState) {
            likeCount++
        } else {
            likeCount--
        }
    }

    // 게시글 보기 페이지로 이동
    val moveToView: () -> Unit = {
        commonViewModel.updatePostUid(photo.uid)
        navController.navigate(Screen.View.route) {
            launchSingleTop = true
            restoreState = true
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = doLike) {
                if (likeState) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "like",
                        modifier = Modifier
                            .size(20.dp),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "like",
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            }
            Text(
                text = "${likeCount}개 좋아요",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { doLike() })

            IconButton(onClick = { commonViewModel.openWriteCommentDialog(photo.uid) }) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = "comment",
                    modifier = Modifier
                        .size(20.dp)
                )
            }
            Text(
                text = "${commentCount}개 댓글",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { moveToView() })
        }

        TextButton(
            onClick = { moveToView() },
        ) {
            Text(
                text = "보기",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(0.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = Screen.View.title,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
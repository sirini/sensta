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
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.domain.model.photo.TsboardPhoto
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.viewmodel.common.LocalCommonViewModel

@Composable
fun PostCardFooter(photo: TsboardPhoto) {
    val navController = LocalNavController.current
    val commonViewModel = LocalCommonViewModel.current
    val doLike: () -> Unit = { }

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
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "like",
                    modifier = Modifier
                        .size(20.dp)
                )
            }
            Text(
                text = "${photo.like}개 좋아요",
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
                text = "${photo.comment}개 댓글",
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
package me.sensta.ui.screen.view.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Delete
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
import androidx.compose.ui.unit.dp
import me.domain.model.board.TsboardPost
import me.sensta.ui.common.CommonDialog
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.local.LocalHomeViewModel
import me.sensta.viewmodel.local.LocalPostViewViewModel

@Composable
fun ViewPostLikeButton(post: TsboardPost) {
    val navController = LocalNavController.current
    val homeViewModel = LocalHomeViewModel.current
    val authViewModel = LocalAuthViewModel.current
    val postViewViewModel = LocalPostViewViewModel.current
    val userInfo by authViewModel.user
    var likeState by remember { mutableStateOf(post.liked) }
    var likeCount by remember { mutableIntStateOf(post.like) }
    var isReallyRemove by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(verticalArrangement = Arrangement.Center) {
                IconButton(onClick = {
                    likeState = !likeState
                    homeViewModel.like(post.uid, likeState)

                    if (likeState) {
                        likeCount++
                    } else {
                        likeCount--
                    }
                }) {
                    if (likeState) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "like",
                            modifier = Modifier
                                .size(40.dp),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "like",
                            modifier = Modifier
                                .size(40.dp)
                        )
                    }
                }
                Text(text = "${likeCount}개 좋아요", style = MaterialTheme.typography.bodySmall)
            }

            if (userInfo.uid == post.writer.uid) {
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    IconButton(onClick = {
                        isReallyRemove = true
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Remove",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Text(
                        text = "삭제하기",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
        }
    }

    if (isReallyRemove) {
        CommonDialog(
            onDismissRequest = { isReallyRemove = false },
            onConfirm = {
                postViewViewModel.remove(post.uid)
                isReallyRemove = false
                navController.navigate(Screen.Home.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = Icons.Outlined.Delete,
            content = {
                Text(
                    text = "작성하신 게시글을 정말로 삭제 할까요?",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        )
    }
}
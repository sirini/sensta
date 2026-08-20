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
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import me.domain.model.board.TsboardPost
import me.sensta.ui.common.CommonDialog
import me.sensta.ui.common.UserReportDialog
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.local.LocalHomeViewModel
import me.sensta.viewmodel.local.LocalPostViewViewModel
import me.sensta.viewmodel.local.LocalUserChatViewModel
import me.sensta.viewmodel.uievent.ChatUiEvent

@Composable
fun ViewPostLikeButton(post: TsboardPost) {
    val navController = LocalNavController.current
    val homeViewModel = LocalHomeViewModel.current
    val authViewModel = LocalAuthViewModel.current
    val postViewViewModel = LocalPostViewViewModel.current
    val userChatViewModel = LocalUserChatViewModel.current
    val context = LocalContext.current
    val userInfo by authViewModel.user
    var likeState by remember { mutableStateOf(post.liked) }
    var likeCount by remember { mutableIntStateOf(post.like) }
    var isReallyRemove by remember { mutableStateOf(false) }
    var isReportDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userChatViewModel.uiEvent.collect { event ->
            when (event) {
                is ChatUiEvent.UserReported -> {
                    Toast.makeText(context, "사진 신고가 접수되었습니다", Toast.LENGTH_SHORT).show()
                }
                is ChatUiEvent.FailedToReport -> {
                    Toast.makeText(context, "사진 신고에 실패했습니다 (${event.message})", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

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
                            contentDescription = "좋아요 취소",
                            modifier = Modifier
                                .size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "좋아요",
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
                            contentDescription = "사진 삭제",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Text(
                        text = "삭제하기",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            } else if (userInfo.token.isNotBlank()) {
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    IconButton(onClick = { isReportDialogVisible = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Report,
                            contentDescription = "사진 신고",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Text(
                        text = "사진 신고",
                        style = MaterialTheme.typography.bodySmall
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


    if (isReportDialogVisible) {
        UserReportDialog(
            title = "사진 신고",
            description = "이 사진이 커뮤니티 운영 원칙을 위반한 이유를 알려주세요.",
            onDismissRequest = { isReportDialogVisible = false },
            onReport = { reason ->
                userChatViewModel.reportUser(
                    targetUserUid = post.writer.uid,
                    content = "사진 #${post.uid} 신고: $reason"
                )
                isReportDialogVisible = false
            }
        )
    }
}

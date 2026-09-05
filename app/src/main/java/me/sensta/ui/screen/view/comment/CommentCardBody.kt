package me.sensta.ui.screen.view.comment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import me.domain.model.auth.hasCompleteSession
import me.domain.model.board.NuboComment
import me.sensta.util.CustomTime
import me.sensta.util.NewlineTagHandler
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.local.LocalCommentViewModel
import me.sensta.viewmodel.local.LocalCommonViewModel
import me.sensta.ui.common.CommonDialog
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.screen.view.ViewPostCommentDialog

@Composable
fun CommentCardBody(comment: NuboComment, likeCount: Int) {
    val authViewModel = LocalAuthViewModel.current
    val commentViewModel = LocalCommentViewModel.current
    val commonViewModel = LocalCommonViewModel.current
    val navController = LocalNavController.current
    val user by authViewModel.user
    var showEditDialog by remember(comment.uid) { mutableStateOf(false) }
    var showDeleteDialog by remember(comment.uid) { mutableStateOf(false) }
    val text = comment.content.parseAsHtml(
        HtmlCompat.FROM_HTML_MODE_LEGACY,
        null,
        NewlineTagHandler()
    ).toString()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(text = text)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${likeCount}개 좋아요",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "${comment.submitted.format(CustomTime.simpleDate)}에 작성",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row {
                if (
                    comment.uid > 0 &&
                    comment.status == 0 &&
                    comment.content != "(deleted)"
                ) {
                    IconButton(onClick = {
                        if (!user.hasCompleteSession) {
                            navController.navigate(Screen.Login.route) { launchSingleTop = true }
                        } else {
                            commonViewModel.openReplyCommentDialog(comment)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.Reply,
                            contentDescription = "${comment.writer.name}님에게 답글",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                if (
                    comment.writer.uid == user.uid &&
                    comment.status == 0 &&
                    comment.content != "(deleted)"
                ) {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "댓글 수정",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = {
                        showDeleteDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "댓글 삭제",
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        ViewPostCommentDialog(
            onDismissRequest = { showEditDialog = false },
            initialText = text,
            label = "수정할 댓글을 입력해 주세요",
            minimumLength = 2
        ) { content ->
            commentViewModel.modify(comment.uid, comment.postUid, content)
            showEditDialog = false
        }
    }

    if (showDeleteDialog) {
        CommonDialog(
            onDismissRequest = { showDeleteDialog = false },
            onConfirm = {
                commentViewModel.remove(
                    removeTargetUid = comment.uid,
                    postUid = comment.postUid
                )
                showDeleteDialog = false
            },
            icon = Icons.Default.Delete
        ) {
            Text("이 댓글을 삭제할까요?")
        }
    }
}

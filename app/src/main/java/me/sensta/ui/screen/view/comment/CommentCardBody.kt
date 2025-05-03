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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import me.domain.model.board.TsboardComment
import me.sensta.util.CustomTime
import me.sensta.util.NewlineTagHandler
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.local.LocalCommentViewModel
import me.sensta.viewmodel.local.LocalCommonViewModel

@Composable
fun CommentCardBody(comment: TsboardComment, likeCount: Int) {
    val context = LocalContext.current
    val authViewModel = LocalAuthViewModel.current
    val commentViewModel = LocalCommentViewModel.current
    val commonViewModel = LocalCommonViewModel.current
    val postUid by commonViewModel.postUid
    val user by authViewModel.user.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        val text = comment.content.parseAsHtml(
            HtmlCompat.FROM_HTML_MODE_LEGACY,
            null,
            NewlineTagHandler()
        ).toString()

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
                if (comment.writer.uid == user.uid) {
                    IconButton(onClick = {
                        commentViewModel.remove(
                            removeTargetUid = comment.uid,
                            postUid = postUid
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "more",
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }
    }
}
package me.sensta.ui.screen.view.comment

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.domain.model.board.NuboComment
import me.sensta.viewmodel.local.LocalCommentViewModel

@Composable
fun CommentCard(comment: NuboComment) {
    val commentViewModel = LocalCommentViewModel.current
    val isReply = comment.replyUid > 0 && comment.replyUid != comment.uid

    Card(
        modifier = Modifier
            .padding(start = if (isReply) 28.dp else 12.dp, end = 12.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        CommentCardHeader(comment, comment.liked, isReply) {
            commentViewModel.like(comment.uid, !comment.liked, comment.like)
        }
        CommentCardBody(comment, comment.like)
    }
    Spacer(modifier = Modifier.height(12.dp))
}

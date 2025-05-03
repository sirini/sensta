package me.sensta.ui.screen.view.comment

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import me.domain.model.board.TsboardComment
import me.sensta.viewmodel.local.LocalCommentViewModel

@Composable
fun CommentCard(comment: TsboardComment) {
    val context = LocalContext.current
    val commentViewModel = LocalCommentViewModel.current

    var likeState by remember { mutableStateOf(comment.liked) }
    var likeCount by remember { mutableIntStateOf(comment.like) }

    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondary
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        CommentCardHeader(comment, likeState) {
            likeState = !likeState
            commentViewModel.like(comment.uid, likeState)

            if (likeState) {
                likeCount++
            } else {
                likeCount--
            }
        }
        CommentCardBody(comment, likeCount)
    }
    Spacer(modifier = Modifier.height(12.dp))
}
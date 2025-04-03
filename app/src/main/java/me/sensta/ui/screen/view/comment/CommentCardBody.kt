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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import me.domain.model.board.TsboardComment
import me.sensta.util.CustomTime
import me.sensta.util.NewlineTagHandler

@Composable
fun CommentCardBody(comment: TsboardComment) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
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
                    text = "${comment.like}개 좋아요",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "${comment.submitted.format(CustomTime.fullDate)}에 작성됨",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row {
                IconButton(onClick = {/* TODO */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "more",
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}
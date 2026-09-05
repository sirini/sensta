package me.sensta.ui.screen.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Textsms
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import me.domain.model.board.NuboComment
import me.sensta.ui.common.CommonDialog
import me.sensta.util.NewlineTagHandler

@Composable
fun ViewPostCommentDialog(
    onDismissRequest: () -> Unit,
    initialText: String = "",
    label: String = "댓글을 입력해 주세요",
    minimumLength: Int = 10,
    replyTarget: NuboComment? = null,
    onConfirm: (String) -> Unit
) {
    var commentText by remember(initialText, replyTarget?.uid) { mutableStateOf(initialText) }
    var showError by remember(replyTarget?.uid) { mutableStateOf(false) }

    CommonDialog(
        onDismissRequest = onDismissRequest,
        onConfirm = {
            if (commentText.trim().length < minimumLength) {
                showError = true
            } else {
                onConfirm(commentText.trim())
            }
        },
        icon = Icons.Default.Textsms,
        confirmText = when {
            replyTarget != null -> "답글 등록"
            initialText.isNotEmpty() -> "수정"
            else -> "댓글 등록"
        }
    ) {
        Column {
            replyTarget?.let { target ->
                val targetContent = target.content.parseAsHtml(
                    HtmlCompat.FROM_HTML_MODE_LEGACY,
                    null,
                    NewlineTagHandler()
                ).toString()
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "${target.writer.name}님에게 답글",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = targetContent,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = commentText,
                onValueChange = {
                    commentText = it
                    if (showError && it.trim().length >= minimumLength) showError = false
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = if (replyTarget == null) label else "답글을 입력해 주세요") },
                isError = showError,
                supportingText = if (showError) {
                    { Text("${minimumLength}자 이상 입력해 주세요") }
                } else {
                    null
                },
            )
        }
    }
}

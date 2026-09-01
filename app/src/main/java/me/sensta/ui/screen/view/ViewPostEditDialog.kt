package me.sensta.ui.screen.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import me.domain.model.board.NuboBoardViewResult
import me.sensta.ui.common.CommonDialog
import me.sensta.util.NewlineTagHandler

@Composable
fun ViewPostEditDialog(
    result: NuboBoardViewResult,
    onDismissRequest: () -> Unit,
    onConfirm: (title: String, content: String, tags: List<String>) -> Unit
) {
    val originalContent = remember(result.post.uid, result.post.content) {
        result.post.content.parseAsHtml(
            HtmlCompat.FROM_HTML_MODE_LEGACY,
            null,
            NewlineTagHandler()
        ).toString().trim()
    }
    var title by remember(result.post.uid) { mutableStateOf(result.post.title) }
    var content by remember(result.post.uid) { mutableStateOf(originalContent) }
    var tagText by remember(result.post.uid) {
        mutableStateOf(result.tags.joinToString(", ") { it.name })
    }
    var showErrors by remember { mutableStateOf(false) }

    val tags = tagText.split(',')
        .map(String::trim)
        .filter(String::isNotEmpty)
        .distinct()

    CommonDialog(
        onDismissRequest = onDismissRequest,
        onConfirm = {
            if (title.trim().length < 2 || content.trim().length < 2) {
                showErrors = true
            } else {
                onConfirm(title.trim(), content.trim(), tags)
            }
        },
        icon = Icons.Outlined.Edit
    ) {
        Column {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("제목") },
                singleLine = true,
                isError = showErrors && title.trim().length < 2
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("내용") },
                minLines = 3,
                maxLines = 8,
                isError = showErrors && content.trim().length < 2
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = tagText,
                onValueChange = { tagText = it },
                label = { Text("태그 (쉼표로 구분)") },
                singleLine = true
            )
            if (showErrors) {
                Text("제목과 내용은 각각 2자 이상 입력해 주세요")
            }
        }
    }
}

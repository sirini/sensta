package me.sensta.ui.screen.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Textsms
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import me.sensta.ui.common.CommonDialog

@Composable
fun ViewPostCommentDialog(
    onDismissRequest: () -> Unit,
    initialText: String = "",
    label: String = "댓글을 입력해 주세요",
    minimumLength: Int = 10,
    onConfirm: (String) -> Unit
) {
    var commentText by remember(initialText) { mutableStateOf(initialText) }
    var showError by remember { mutableStateOf(false) }

    CommonDialog(
        onDismissRequest = onDismissRequest,
        onConfirm = {
            if (commentText.trim().length < minimumLength) {
                showError = true
            } else {
                onConfirm(commentText.trim())
            }
        },
        icon = Icons.Default.Textsms
    ) {
        OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            label = { Text(text = label) },
            isError = showError,
            supportingText = if (showError) {
                { Text("${minimumLength}자 이상 입력해 주세요") }
            } else {
                null
            },
        )
    }
}

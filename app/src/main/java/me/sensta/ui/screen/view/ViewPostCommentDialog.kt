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
    onConfirm: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }

    CommonDialog(
        onDismissRequest = onDismissRequest,
        onConfirm = { onConfirm(commentText) },
        icon = Icons.Default.Textsms
    ) {
        OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            label = { Text(text = "댓글을 입력해 주세요") },
        )
    }
}
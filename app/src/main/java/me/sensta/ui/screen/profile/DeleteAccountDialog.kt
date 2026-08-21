package me.sensta.ui.screen.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun DeleteAccountDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    var confirmation by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(
                Icons.Outlined.DeleteForever,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text("계정을 영구 삭제할까요?") },
        text = {
            androidx.compose.foundation.layout.Column {
                Text("사진, 댓글, 좋아요, 대화와 계정 정보가 모두 삭제되며 복구할 수 없습니다.")
                OutlinedTextField(
                    value = confirmation,
                    onValueChange = { confirmation = it.take(6) },
                    label = { Text("확인을 위해 DELETE 입력") },
                    singleLine = true
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("취소") }
        },
        confirmButton = {
            TextButton(
                enabled = confirmation == "DELETE",
                onClick = onConfirm
            ) {
                Text("영구 삭제", color = MaterialTheme.colorScheme.error)
            }
        }
    )
}

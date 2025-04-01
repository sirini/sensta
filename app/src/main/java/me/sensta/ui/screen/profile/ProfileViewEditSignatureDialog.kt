package me.sensta.ui.screen.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import me.sensta.ui.common.CommonDialog

@Composable
fun ProfileViewEditSignatureDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newSignature by remember { mutableStateOf("") }

    CommonDialog(
        onDismissRequest = onDismissRequest,
        onConfirm = { onConfirm(newSignature) },
        icon = Icons.Default.EditNote
    ) {
        OutlinedTextField(
            value = newSignature,
            onValueChange = { newSignature = it },
            label = { Text(text = "변경할 서명을 입력해 주세요") },
        )
    }
}
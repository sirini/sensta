package me.sensta.ui.screen.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import me.sensta.ui.common.CommonDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileViewEditNameDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newName by remember { mutableStateOf("") }

    CommonDialog(
        onDismissRequest = onDismissRequest,
        onConfirm = { onConfirm(newName) },
        icon = Icons.Default.Edit
    ) {
        OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text(text = "변경할 이름을 입력해 주세요") },
        )
    }
}
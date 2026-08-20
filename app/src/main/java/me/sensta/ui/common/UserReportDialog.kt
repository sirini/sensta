package me.sensta.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun UserReportDialog(
    title: String,
    description: String,
    onDismissRequest: () -> Unit,
    onReport: (String) -> Unit
) {
    var reason by remember { mutableStateOf("") }
    val normalizedReason = reason.trim()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = { Icon(Icons.Outlined.Report, contentDescription = null) },
        title = { Text(title) },
        text = {
            androidx.compose.foundation.layout.Column {
                Text(description)
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it.take(MAX_REPORT_LENGTH) },
                    label = { Text("신고 사유") },
                    supportingText = { Text("${reason.length}/$MAX_REPORT_LENGTH") },
                    minLines = 3
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("취소") }
        },
        confirmButton = {
            TextButton(
                enabled = normalizedReason.length >= MIN_REPORT_LENGTH,
                onClick = { onReport(normalizedReason) }
            ) {
                Text("신고 접수")
            }
        }
    )
}

private const val MIN_REPORT_LENGTH = 5
private const val MAX_REPORT_LENGTH = 500

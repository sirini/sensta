package me.sensta.ui.screen.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.sensta.viewmodel.local.LocalAuthViewModel
import java.time.LocalDateTime

@Composable
fun ChatMyMessage(
    message: String,
    timestamp: LocalDateTime,
    showReadState: Boolean,
    isRead: Boolean,
    onHashtagClick: (String) -> Unit
) {
    val authViewModel = LocalAuthViewModel.current
    val my by authViewModel.user

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.6f)
                .padding(4.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                ChatMessageText(message = message, onHashtagClick = onHashtagClick)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatChatTimestamp(timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (showReadState) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRead) "읽음" else "전송됨",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        ChatAvatar(
            profile = my.profile,
            name = my.name,
            backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    }
}

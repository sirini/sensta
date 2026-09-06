package me.sensta.ui.screen.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import me.sensta.viewmodel.local.LocalUserChatViewModel

@Composable
fun ChatOtherUserMessage(
    message: String,
    onHashtagClick: (String) -> Unit
) {
    val userViewModel = LocalUserChatViewModel.current
    val otherUser by userViewModel.otherUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChatAvatar(
            profile = otherUser.profile,
            name = otherUser.name,
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.5f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.6f)
                .padding(4.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Box(modifier = Modifier.padding(8.dp)) {
                ChatMessageText(message = message, onHashtagClick = onHashtagClick)
            }
        }
    }
}

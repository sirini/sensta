package me.sensta.ui.screen.view.content

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.viewmodel.local.LocalCommonViewModel

@Composable
fun ViewPostCenterButtons(postUid: Int) {
    val navController = LocalNavController.current
    val commonViewModel = LocalCommonViewModel.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                    contentDescription = "back"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("뒤로", fontWeight = FontWeight.Bold)
            }
        }

        Button(
            onClick = { commonViewModel.openWriteCommentDialog(postUid) },
            modifier = Modifier
                .weight(2f)
                .padding(start = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ChatBubble,
                    contentDescription = "chat"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("댓글 남기기", fontWeight = FontWeight.Bold)
            }
        }
    }
}
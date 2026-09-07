package me.sensta.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.domain.model.user.NuboChatThread
import me.domain.repository.NuboResponse
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.screen.user.ChatAvatar
import me.sensta.util.CustomTime
import me.sensta.util.convertHtmlToText
import me.sensta.viewmodel.local.LocalUserChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectMessageListScreen() {
    val navController = LocalNavController.current
    val userViewModel = LocalUserChatViewModel.current
    val response by userViewModel.chatThreads
    val isLoading by userViewModel.isLoadingThreads
    val pullState = rememberPullToRefreshState()

    LaunchedEffect(Unit) { userViewModel.loadChatThreads() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullToRefresh(
                state = pullState,
                isRefreshing = isLoading,
                onRefresh = { userViewModel.loadChatThreads() }
            )
    ) {
        when (val current = response) {
            NuboResponse.Loading -> LoadingScreen()
            is NuboResponse.Error -> MessageListNotice(
                title = "메시지 목록을 불러오지 못했습니다",
                description = "인터넷 연결을 확인한 뒤 다시 시도해 주세요",
                action = { userViewModel.loadChatThreads() }
            )
            is NuboResponse.Success -> if (current.data.isEmpty()) {
                MessageListNotice(
                    title = "아직 받은 메시지가 없습니다",
                    description = "사진가 프로필에서 1:1 대화를 시작할 수 있습니다"
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(current.data, key = { it.senderUid }) { thread ->
                        DirectMessageThreadRow(thread) {
                            userViewModel.loadOtherUserInfo(thread.senderUid)
                            navController.navigate(Screen.UserMessage.route) { launchSingleTop = true }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
                    }
                }
            }
        }

        if (isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun MessageListNotice(
    title: String,
    description: String,
    action: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(
            description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
        action?.let {
            TextButton(onClick = it, modifier = Modifier.padding(top = 8.dp)) { Text("다시 시도") }
        }
    }
}

@Composable
private fun DirectMessageThreadRow(thread: NuboChatThread, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChatAvatar(thread.senderProfile, thread.senderName, size = 48.dp)
        Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    thread.senderName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    thread.timestamp.format(CustomTime.commentDate),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                convertHtmlToText(thread.latestMessage),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

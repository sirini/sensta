package me.sensta.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import me.sensta.ui.common.LocalScrollBehavior
import me.sensta.ui.screen.user.ChatInputBar
import me.sensta.ui.screen.user.ChatMyMessage
import me.sensta.ui.screen.user.ChatOtherUserMessage
import me.sensta.ui.screen.user.LatestMessageDivider
import me.sensta.ui.screen.user.OtherUserInfo
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.local.LocalUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen() {
    val context = LocalContext.current
    val scrollBehavior = LocalScrollBehavior.current
    val userViewModel = LocalUserViewModel.current
    val authViewModel = LocalAuthViewModel.current
    val listState = rememberLazyListState()
    val chatHistory by userViewModel.chatHistory.collectAsState()
    val my by authViewModel.user.collectAsState()
    val isLoadingChat by userViewModel.isLoadingChat

    // 스크롤 상태를 초기화해서 topBar가 펼쳐진 상태로 만들기
    LaunchedEffect(Unit) {
        scrollBehavior.state.heightOffset = 0f
        userViewModel.loadChatHistory(context)

        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.lastIndex)
        }
    }

    // 메시지가 추가될 때 아래로 스크롤
    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.lastIndex)
        }
    }

    Scaffold(
        bottomBar = { ChatInputBar() },
    ) {
        if (isLoadingChat) {
            LoadingScreen()
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    OtherUserInfo()
                    LatestMessageDivider()
                }
                items(chatHistory) { chat ->
                    if (chat.userUid == my.uid) {
                        ChatMyMessage(message = chat.message)
                    } else {
                        ChatOtherUserMessage(message = chat.message)
                    }
                }
                item {
                    Box(modifier = Modifier.padding(it))
                }
            }
        }
    }
}

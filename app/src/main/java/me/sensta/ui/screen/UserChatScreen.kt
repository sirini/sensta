package me.sensta.ui.screen

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import me.sensta.util.convertHtmlToText
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.local.LocalUserChatViewModel
import me.sensta.viewmodel.uievent.ChatUiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserChatScreen() {
    val context = LocalContext.current
    val scrollBehavior = LocalScrollBehavior.current
    val userViewModel = LocalUserChatViewModel.current
    val authViewModel = LocalAuthViewModel.current
    val listState = rememberLazyListState()
    val chatHistory by userViewModel.chatHistory.collectAsState()
    val my by authViewModel.user
    val isLoadingChat by userViewModel.isLoadingChat
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(Unit) {
        // 스크롤 상태를 초기화해서 topBar가 펼쳐진 상태로 만들기
        scrollBehavior.state.heightOffset = 0f

        // 대화 내역 가져와서 제일 하단으로 스크롤해주기
        userViewModel.loadChatHistory()
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.lastIndex)
        }

        // UserViewModel에서 전달된 이벤트들에 따라 메시지 출력하기
        userViewModel.uiEvent.collect { event ->
            when (event) {
                is ChatUiEvent.FailedToSendChat -> {
                    Toast.makeText(context, "메시지 전송에 실패했습니다", Toast.LENGTH_SHORT).show()
                }
            }
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
        Crossfade(targetState = !isLoadingChat) { visible ->
            if (visible) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pullToRefresh(
                            state = pullToRefreshState,
                            isRefreshing = isLoadingChat,
                            onRefresh = {
                                userViewModel.loadChatHistory()
                                Toast.makeText(context, "대화 내역을 불러왔습니다.", Toast.LENGTH_SHORT).show()
                            }
                        )
                ) {
                    // 상대와의 대화 내역 보여주기
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            OtherUserInfo()
                            LatestMessageDivider()
                        }
                        items(chatHistory) { chat ->
                            val message = convertHtmlToText(chat.message)
                            if (chat.userUid == my.uid) {
                                ChatMyMessage(message = message)
                            } else {
                                ChatOtherUserMessage(message = message)
                            }
                        }
                        item {
                            Box(modifier = Modifier.padding(it))
                        }
                    }

                    // 당겨서 새로고침 중일 때 로딩 화면 제공
                    if (isLoadingChat) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            } else {
                LoadingScreen()
            }
        }
    }
}

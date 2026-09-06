package me.sensta.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import me.domain.repository.NuboResponse
import me.sensta.ui.common.LocalScrollBehavior
import me.sensta.ui.screen.user.ChatInputBar
import me.sensta.ui.screen.user.ChatMyMessage
import me.sensta.ui.screen.user.ChatOtherUserMessage
import me.sensta.ui.screen.user.LatestMessageDivider
import me.sensta.ui.screen.user.OtherUserInfo
import me.sensta.ui.screen.user.UserPhotoGrid
import me.sensta.util.convertHtmlToText
import me.sensta.util.toPreviewImagePath
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.local.LocalUserChatViewModel
import me.sensta.viewmodel.uievent.ChatUiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserChatScreen(
    initialUserUid: Int = 0,
    openMessageInitially: Boolean = initialUserUid > 0
) {
    val context = LocalContext.current
    val scrollBehavior = LocalScrollBehavior.current
    val userViewModel = LocalUserChatViewModel.current
    val userPosts by userViewModel.userPosts
    val otherUser by userViewModel.otherUser
    val startsInMessage = shouldOpenMessageInitially(initialUserUid, openMessageInitially)
    val selectedTab = rememberSaveable(initialUserUid, openMessageInitially) {
        mutableIntStateOf(if (startsInMessage) MESSAGE_TAB else PHOTO_TAB)
    }
    var showProfileHeader by rememberSaveable(initialUserUid, openMessageInitially) {
        mutableStateOf(!startsInMessage)
    }
    val scrollAccumulator = remember { mutableFloatStateOf(0f) }
    val latestPhoto = (userPosts as? NuboResponse.Success)
        ?.data
        ?.firstOrNull()
        ?.cover
        ?.toPreviewImagePath()
    val profileScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source != NestedScrollSource.UserInput || available.y == 0f) return Offset.Zero

                val changedDirection =
                    scrollAccumulator.floatValue != 0f &&
                        (scrollAccumulator.floatValue > 0f) != (available.y > 0f)
                if (changedDirection) scrollAccumulator.floatValue = 0f
                scrollAccumulator.floatValue += available.y

                when {
                    scrollAccumulator.floatValue <= -PROFILE_HEADER_SCROLL_THRESHOLD -> {
                        showProfileHeader = false
                        scrollAccumulator.floatValue = 0f
                    }
                    scrollAccumulator.floatValue >= PROFILE_HEADER_SCROLL_THRESHOLD -> {
                        showProfileHeader = true
                        scrollAccumulator.floatValue = 0f
                    }
                }
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(Unit) {
        scrollBehavior.state.heightOffset = 0f
        if (initialUserUid > 0) userViewModel.loadOtherUserInfo(initialUserUid)

        userViewModel.uiEvent.collect { event ->
            when (event) {
                is ChatUiEvent.FailedToSendChat ->
                    Toast.makeText(context, "메시지 전송에 실패했습니다", Toast.LENGTH_SHORT).show()
                is ChatUiEvent.UserReported ->
                    Toast.makeText(context, "신고가 접수되었습니다", Toast.LENGTH_SHORT).show()
                is ChatUiEvent.FailedToReport ->
                    Toast.makeText(context, "신고 접수에 실패했습니다 (${event.message})", Toast.LENGTH_SHORT).show()
                is ChatUiEvent.UserBlocked ->
                    Toast.makeText(context, "사용자를 차단했습니다", Toast.LENGTH_SHORT).show()
                is ChatUiEvent.UserUnblocked ->
                    Toast.makeText(context, "차단을 해제했습니다", Toast.LENGTH_SHORT).show()
                is ChatUiEvent.FailedToChangeBlock ->
                    Toast.makeText(context, "차단 설정을 바꾸지 못했습니다 (${event.message})", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(selectedTab.intValue, otherUser.uid) {
        if (selectedTab.intValue == MESSAGE_TAB && otherUser.uid > 0) {
            userViewModel.loadChatHistory()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(profileScrollConnection)
    ) {
        AnimatedVisibility(
            visible = showProfileHeader,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            OtherUserInfo(latestPhoto = latestPhoto)
        }

        PrimaryTabRow(selectedTabIndex = selectedTab.intValue) {
            Tab(
                selected = selectedTab.intValue == PHOTO_TAB,
                onClick = { selectedTab.intValue = PHOTO_TAB },
                text = { Text("사진") }
            )
            Tab(
                selected = selectedTab.intValue == MESSAGE_TAB,
                onClick = { selectedTab.intValue = MESSAGE_TAB },
                text = { Text("1:1 메시지") }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab.intValue) {
                PHOTO_TAB -> when (val response = userPosts) {
                    is NuboResponse.Loading -> LoadingScreen()
                    is NuboResponse.Success -> UserPhotoGrid(response.data)
                    is NuboResponse.Error -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "사진 목록을 불러오지 못했습니다.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                MESSAGE_TAB -> UserMessageTab()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserMessageTab() {
    val context = LocalContext.current
    val userViewModel = LocalUserChatViewModel.current
    val authViewModel = LocalAuthViewModel.current
    val listState = rememberLazyListState()
    val chatHistory by userViewModel.chatHistory.collectAsState()
    val my by authViewModel.user
    val isLoadingChat by userViewModel.isLoadingChat
    val isBlockedByMe by userViewModel.isBlockedByMe
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) listState.animateScrollToItem(chatHistory.lastIndex + 1)
    }

    Scaffold(bottomBar = { if (!isBlockedByMe) ChatInputBar() }) { innerPadding ->
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
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                item { LatestMessageDivider() }
                if (isBlockedByMe) {
                    item {
                        Text(
                            text = "차단한 사용자의 대화는 표시하지 않습니다.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                items(if (isBlockedByMe) emptyList() else chatHistory) { chat ->
                    val message = convertHtmlToText(chat.message)
                    if (chat.userUid == my.uid) {
                        ChatMyMessage(message = message)
                    } else {
                        ChatOtherUserMessage(message = message)
                    }
                }
                item { Box(modifier = Modifier.padding(innerPadding)) }
            }

            if (isLoadingChat) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

private const val PHOTO_TAB = 0
private const val MESSAGE_TAB = 1
private const val PROFILE_HEADER_SCROLL_THRESHOLD = 42f

internal fun shouldOpenMessageInitially(
    initialUserUid: Int,
    openMessageInitially: Boolean
): Boolean = initialUserUid > 0 || openMessageInitially

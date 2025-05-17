package me.sensta.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import me.domain.repository.TsboardResponse
import me.sensta.ui.common.LocalScrollBehavior
import me.sensta.ui.screen.explorer.GridImage
import me.sensta.ui.screen.explorer.RecentHashtag
import me.sensta.ui.screen.explorer.SearchBox
import me.sensta.viewmodel.local.LocalExplorerViewModel
import me.sensta.viewmodel.uievent.ExplorerUiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorerScreen() {
    val context = LocalContext.current
    val scrollBehavior = LocalScrollBehavior.current
    val explorerViewModel = LocalExplorerViewModel.current
    val isLoading by remember { mutableStateOf(false) }
    val posts by explorerViewModel.posts
    val pullToRefreshState = rememberPullToRefreshState()
    var notFound by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // 스크롤 상태를 초기화해서 topBar가 펼쳐진 상태로 만들기
        scrollBehavior.state.heightOffset = 0f

        // ExplorerViewModel에서 전달된 이벤트들에 따라 메시지 출력하기
        explorerViewModel.uiEvent.collect { event ->
            when (event) {
                is ExplorerUiEvent.UnableToFindPosts -> {
                    Toast.makeText(context, "게시글을 더 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
                    notFound = true
                }

                is ExplorerUiEvent.UnableToFindRecentHashtags -> {
                    Toast.makeText(
                        context,
                        "최근 해시태그를 조회하지 못했습니다 (${event.message})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullToRefresh(
                state = pullToRefreshState,
                isRefreshing = isLoading,
                onRefresh = { explorerViewModel.search(0, "") }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBox()
            RecentHashtag()

            when (val postResponse = posts) {
                is TsboardResponse.Loading -> {
                    if (!notFound) {
                        LoadingScreen()
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                        Icon(
                            imageVector = Icons.Default.BrokenImage,
                            contentDescription = "Not found",
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "게시글을 찾을 수 없습니다")
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(onClick = {
                            explorerViewModel.search(0, "")
                            notFound = false
                        }) {
                            Text(text = "검색 초기화")
                        }
                    }
                }

                is TsboardResponse.Success -> {
                    GridImage(postResponse.data)
                    notFound = false
                }

                is TsboardResponse.Error -> ErrorScreen()
            }
        }

        // 당겨서 새로고침 중일 때 로딩 화면 제공
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}
package me.sensta.ui.screen.home

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import me.domain.model.photo.TsboardPhoto
import me.sensta.ui.screen.home.post.PostCard
import me.sensta.viewmodel.local.LocalHomeViewModel

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun PhotoList(photos: List<TsboardPhoto>) {
    val context = LocalContext.current
    val homeViewModel = LocalHomeViewModel.current
    val isLoading by homeViewModel.isLoadingMore
    val bunch by homeViewModel.bunch
    val page by homeViewModel.page
    val listState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()

    // 스크롤 상태를 감시해서 마지막 항목에 도달하면 이전 사진들 불러오기
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .debounce(500)
            .distinctUntilChanged()
            .collect { index ->
                index?.let {
                    if (index >= (bunch * page) - 1) {
                        homeViewModel.refresh()
                        Toast.makeText(context, "이전 사진들을 불러왔습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    Crossfade(targetState = !isLoading) { visible ->
        if (visible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullToRefresh(
                        state = pullToRefreshState,
                        isRefreshing = isLoading,
                        onRefresh = {
                            homeViewModel.refresh(resetLastUid = true)
                            Toast.makeText(context, "최근 사진들을 불러왔습니다.", Toast.LENGTH_SHORT).show()
                        }
                    )
            ) {
                // 사진 목록 보여주기
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(photos) { photo ->
                        PostCard(photo = photo)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // 당겨서 새로고침 중일 때 로딩 화면 제공
                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

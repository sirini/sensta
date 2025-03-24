package me.sensta.ui.screen.home

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import me.domain.model.photo.TsboardPhoto
import me.domain.repository.TsboardResponse
import me.sensta.ui.screen.home.post.PostCard
import me.sensta.viewmodel.PhotoViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class, FlowPreview::class)
@Composable
fun PhotoList(
    photoResponse: TsboardResponse<List<TsboardPhoto>>
) {
    val context = LocalContext.current
    val photoViewModel: PhotoViewModel = hiltViewModel()
    val isLoading by photoViewModel.isLoadingMore.collectAsState()
    val photos = (photoResponse as TsboardResponse.Success<List<TsboardPhoto>>).data
    val listState = rememberLazyListState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = {
            photoViewModel.refresh(resetLastUid = true)
            Toast.makeText(context, "최근 사진들을 불러왔습니다.", Toast.LENGTH_SHORT).show()
        }
    )

    // 스크롤 상태를 감시해서 마지막 항목에 도달하면 이전 사진들 불러오기
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .debounce(500)
            .distinctUntilChanged()
            .collect { index ->
                index?.let {
                    if (index >= (photos.size * photoViewModel.page.value) - 1) {
                        photoViewModel.refresh()
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
                    .pullRefresh(pullRefreshState)
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
                PullRefreshIndicator(
                    refreshing = isLoading,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
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

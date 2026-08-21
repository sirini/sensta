package me.sensta.ui.screen.home

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import me.domain.model.board.TsboardPost
import me.sensta.ui.screen.home.post.PostCard
import me.sensta.viewmodel.local.LocalHomeViewModel
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoList(posts: List<TsboardPost>) {
    val context = LocalContext.current
    val homeViewModel = LocalHomeViewModel.current
    val isLoading by homeViewModel.isLoadingMore
    val feedIndex by homeViewModel.feedIndex
    val pagerState = rememberPagerState(
        initialPage = feedIndex.coerceIn(0, posts.lastIndex.coerceAtLeast(0)),
        pageCount = { posts.size }
    )
    val pullToRefreshState = rememberPullToRefreshState()

    // 마지막 작품에 가까워지면 다음 묶음을 미리 불러온다.
    LaunchedEffect(pagerState.currentPage, posts.size) {
        if (posts.isNotEmpty() && pagerState.currentPage >= posts.lastIndex - 3) {
            homeViewModel.refresh()
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect(homeViewModel::updateFeedIndex)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullToRefresh(
                state = pullToRefreshState,
                isRefreshing = isLoading,
                onRefresh = {
                    homeViewModel.refresh(resetPaging = true)
                    Toast.makeText(context, "최근 사진들을 불러왔습니다.", Toast.LENGTH_SHORT).show()
                }
            )
    ) {
        VerticalPager(
            state = pagerState,
            key = { posts[it].uid },
            modifier = Modifier.fillMaxSize()
        ) { page ->
            PostCard(post = posts[page])
        }

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}

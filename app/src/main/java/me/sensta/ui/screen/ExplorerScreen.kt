package me.sensta.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import me.domain.repository.TsboardResponse
import me.sensta.ui.common.LocalScrollBehavior
import me.sensta.ui.screen.explorer.GridImage
import me.sensta.ui.screen.explorer.SearchBox
import me.sensta.viewmodel.ExplorerViewModel
import me.sensta.viewmodel.local.LocalNotificationViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ExplorerScreen() {
    val notiViewModel = LocalNotificationViewModel.current
    val scrollBehavior = LocalScrollBehavior.current
    val explorerViewModel: ExplorerViewModel = hiltViewModel()
    val isLoading by remember { mutableStateOf(false) }
    val posts by explorerViewModel.posts
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = { explorerViewModel.search(0, "") }
    )

    // 스크롤 상태를 초기화해서 topBar가 펼쳐진 상태로 만들기
    LaunchedEffect(Unit) {
        scrollBehavior.state.heightOffset = 0f
        notiViewModel.loadNotifications()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBox()

            when (val postResponse = posts) {
                is TsboardResponse.Loading -> LoadingScreen()
                is TsboardResponse.Success -> GridImage(postResponse.data)
                is TsboardResponse.Error -> ErrorScreen()
            }
        }

        // 당겨서 새로고침 중일 때 로딩 화면 제공
        PullRefreshIndicator(
            refreshing = isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

}
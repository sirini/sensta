package me.sensta.ui.screen.explorer

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import me.data.env.Env
import me.domain.model.board.TsboardPost
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.viewmodel.local.LocalCommonViewModel
import me.sensta.viewmodel.local.LocalExplorerViewModel

@OptIn(FlowPreview::class)
@Composable
fun GridImage(posts: List<TsboardPost>) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val commonViewModel = LocalCommonViewModel.current
    val explorerViewModel = LocalExplorerViewModel.current
    val gridState = rememberLazyGridState()
    val bunch by explorerViewModel.bunch
    val page by explorerViewModel.page

    // 스크롤 상태를 감시해서 마지막 항목에 도달하면 이전 사진들 불러오기
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .debounce(500)
            .distinctUntilChanged()
            .collect { index ->
                index?.let {
                    if (index >= (bunch * page) - 1) {
                        explorerViewModel.refresh(resetLastUid = false)
                        Toast.makeText(context, "이전 사진들을 불러왔습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(posts) { post ->
            AsyncImage(
                model = Env.DOMAIN + post.cover,
                contentDescription = post.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable {
                        commonViewModel.updatePostUid(post.uid)
                        navController.navigate(Screen.View.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
            )
        }
    }
}
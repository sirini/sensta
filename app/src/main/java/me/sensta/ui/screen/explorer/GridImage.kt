package me.sensta.ui.screen.explorer

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import me.data.env.Env
import me.domain.model.board.NuboPost
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.theme.LocalSenstaExtendedColors
import me.sensta.viewmodel.local.LocalCommonViewModel
import me.sensta.viewmodel.local.LocalExplorerViewModel

@OptIn(FlowPreview::class)
@Composable
fun GridImage(posts: List<NuboPost>) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val commonViewModel = LocalCommonViewModel.current
    val explorerViewModel = LocalExplorerViewModel.current
    val gridState = rememberLazyGridState()
    val extendedColors = LocalSenstaExtendedColors.current

    // 스크롤 상태를 감시해서 마지막 항목에 도달하면 이전 사진들 불러오기
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .debounce(500)
            .distinctUntilChanged()
            .collect { index ->
                index?.let {
                    if (posts.isNotEmpty() && index >= posts.lastIndex - 6) {
                        explorerViewModel.refresh(resetPaging = false)
                        Toast.makeText(context, "이전 사진들을 불러왔습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(posts, key = { it.uid }) { post ->
            Box(
                modifier = Modifier
                    .aspectRatio(0.8f)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        commonViewModel.updatePostUid(post.uid)
                        navController.navigate(Screen.View.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
            ) {
                AsyncImage(
                    model = Env.DOMAIN + post.cover,
                    contentDescription = post.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.82f))
                            )
                        )
                        .padding(start = 12.dp, end = 12.dp, top = 28.dp, bottom = 10.dp)
                ) {
                    Column {
                        Text(
                            text = post.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = extendedColors.onMedia,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = post.writer.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = extendedColors.onMedia.copy(alpha = 0.78f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

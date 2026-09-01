package me.sensta.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.distinctUntilChanged
import me.data.env.Env
import me.domain.model.board.NuboStudioPost
import me.domain.model.board.NuboStudioSort
import me.sensta.util.CustomTime
import me.sensta.viewmodel.ProfileStudioUiState

@Composable
fun ProfileStudioTab(
    state: ProfileStudioUiState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onSelectSort: (NuboStudioSort) -> Unit,
    onPostClick: (Int) -> Unit,
    onUploadClick: () -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, state.posts.size, state.hasNext) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastIndex ->
                if (lastIndex != null && state.posts.isNotEmpty() && lastIndex >= state.posts.lastIndex - 2) {
                    onLoadMore()
                }
            }
    }

    when {
        state.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        state.error != null && state.posts.isEmpty() -> StudioMessage(
            title = "작품 정보를 불러오지 못했습니다.",
            action = "다시 시도",
            onAction = onRefresh
        )

        state.posts.isEmpty() -> StudioMessage(
            title = "아직 업로드한 작품이 없습니다.",
            action = "첫 사진 업로드",
            onAction = onUploadClick
        )

        else -> LazyColumn(
            state = listState,
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 10.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(key = "sort") {
                StudioSortRow(state.selectedSort, onSelectSort)
            }
            items(state.posts, key = { it.uid }) { post ->
                StudioPostCard(post = post, onClick = { onPostClick(post.uid) })
            }
            if (state.isLoadingMore) {
                item(key = "loading-more") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            } else if (state.error != null) {
                item(key = "load-error") {
                    OutlinedButton(onClick = onLoadMore, modifier = Modifier.fillMaxWidth()) {
                        Text("다음 작품 다시 불러오기")
                    }
                }
            }
        }
    }
}

@Composable
private fun StudioSortRow(
    selected: NuboStudioSort,
    onSelect: (NuboStudioSort) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NuboStudioSort.entries.forEach { sort ->
            FilterChip(
                selected = selected == sort,
                onClick = { onSelect(sort) },
                label = {
                    Text(
                        when (sort) {
                            NuboStudioSort.Recent -> "최신순"
                            NuboStudioSort.Views -> "조회순"
                            NuboStudioSort.Likes -> "좋아요순"
                            NuboStudioSort.Comments -> "댓글순"
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun StudioPostCard(post: NuboStudioPost, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 112.dp, height = 88.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                if (post.cover.isBlank()) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoLibrary,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    AsyncImage(
                        model = Env.DOMAIN + post.cover,
                        contentDescription = post.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                if (post.imageCount > 1) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.72f),
                        contentColor = Color.White,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(5.dp)
                    ) {
                        Text(
                            text = "사진 ${post.imageCount.toCountText()}",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = post.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (post.status == SECRET_STATUS) {
                        Text(
                            text = "비공개",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text(
                    text = "${post.submitted.format(CustomTime.simpleDate)} 업로드",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StudioPostMetric(Icons.Outlined.Visibility, "조회", post.hit)
                    StudioPostMetric(Icons.Outlined.FavoriteBorder, "좋아요", post.like)
                    StudioPostMetric(Icons.Outlined.ChatBubbleOutline, "댓글", post.comment)
                }
            }
        }
    }
}

@Composable
private fun StudioPostMetric(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: Long
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.semantics {
            contentDescription = "$label ${value.toCountText()}"
        }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = value.toCountText(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StudioMessage(
    title: String,
    action: String,
    onAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.PhotoLibrary,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onAction) {
            Text(action)
        }
    }
}

private const val SECRET_STATUS = 2

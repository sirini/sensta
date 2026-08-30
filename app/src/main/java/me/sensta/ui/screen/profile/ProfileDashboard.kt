package me.sensta.ui.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.domain.model.board.NuboStudioSort
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.viewmodel.ProfileStudioUiState
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.local.LocalCommonViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(
    studio: ProfileStudioUiState,
    onRefreshStudio: () -> Unit,
    onLoadMoreStudio: () -> Unit,
    onSelectSort: (NuboStudioSort) -> Unit
) {
    val authViewModel = LocalAuthViewModel.current
    val commonViewModel = LocalCommonViewModel.current
    val navController = LocalNavController.current
    val user by authViewModel.user
    val selectedTab = rememberSaveable { mutableIntStateOf(STUDIO_TAB) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileViewImage(imageSize = 104.dp)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SENSTA PHOTOGRAPHER",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = user.signature.ifBlank { "사진으로 일상의 순간을 기록하고 있습니다." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        StudioSummary(studio)

        PrimaryTabRow(selectedTabIndex = selectedTab.intValue) {
            Tab(
                selected = selectedTab.intValue == STUDIO_TAB,
                onClick = { selectedTab.intValue = STUDIO_TAB },
                text = { Text("내 작품") }
            )
            Tab(
                selected = selectedTab.intValue == INFO_TAB,
                onClick = { selectedTab.intValue = INFO_TAB },
                text = { Text("내 정보") }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab.intValue) {
                STUDIO_TAB -> ProfileStudioTab(
                    state = studio,
                    onRefresh = onRefreshStudio,
                    onLoadMore = onLoadMoreStudio,
                    onSelectSort = onSelectSort,
                    onPostClick = { postUid ->
                        commonViewModel.updatePostUid(postUid)
                        navController.navigate(Screen.View.route) {
                            launchSingleTop = true
                        }
                    },
                    onUploadClick = {
                        navController.navigate(Screen.Upload.route) {
                            launchSingleTop = true
                        }
                    }
                )
                INFO_TAB -> ProfileInfoTab()
            }
        }
    }
}

@Composable
private fun StudioSummary(state: ProfileStudioUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProfileMetric("작품", state.summary.postCount, state.isLoading)
            ProfileMetric("사진", state.summary.photoCount, state.isLoading)
            ProfileMetric("받은 좋아요", state.summary.likeCount, state.isLoading)
        }
        Text(
            text = if (state.isLoading) {
                "누적 조회 — · 댓글 —"
            } else {
                "누적 조회 ${state.summary.viewCount.toCountText()} · 댓글 ${state.summary.commentCount.toCountText()}"
            },
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 6.dp)
        )
    }
}

@Composable
private fun ProfileMetric(label: String, value: Long, isLoading: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (isLoading) "—" else value.toCountText(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

internal fun Long.toCountText(): String = String.format(Locale.KOREAN, "%,d", this)

private const val STUDIO_TAB = 0
private const val INFO_TAB = 1

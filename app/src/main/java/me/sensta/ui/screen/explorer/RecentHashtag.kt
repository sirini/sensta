package me.sensta.ui.screen.explorer

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.domain.model.common.TsboardTag
import me.sensta.ui.screen.view.content.ViewPostTag
import me.sensta.viewmodel.local.LocalExplorerViewModel

@Composable
fun RecentHashtag() {
    val explorerViewModel = LocalExplorerViewModel.current
    val recentHashtags by explorerViewModel.recentHashtags
    val horizontalScrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        explorerViewModel.loadRecentHashtags()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(horizontalScrollState)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            recentHashtags.forEach { t ->
                ViewPostTag(TsboardTag(uid = t.uid, name = t.name)) {
                    explorerViewModel.search(
                        option = explorerViewModel.hashtagOption,
                        keyword = t.name
                    )
                }
            }
        }

        // 왼쪽 그라디언트
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(24.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(MaterialTheme.colorScheme.background, Color.Transparent)
                    )
                )
                .align(Alignment.CenterStart)
        )

        // 오른쪽 그라디언트
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(24.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                    )
                )
                .align(Alignment.CenterEnd)
        )
    }


}
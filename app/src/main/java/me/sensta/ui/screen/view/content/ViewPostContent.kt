package me.sensta.ui.screen.view.content

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import me.domain.model.board.TsboardBoardViewResult
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.util.CustomTime
import me.sensta.util.NewlineTagHandler
import me.sensta.viewmodel.local.LocalExplorerViewModel
import java.util.Locale

@Composable
fun ViewPostContent(result: TsboardBoardViewResult) {
    val navController = LocalNavController.current
    val explorerViewModel = LocalExplorerViewModel.current
    val tagScrollState = rememberScrollState()
    val text = result.post.content.parseAsHtml(
        HtmlCompat.FROM_HTML_MODE_LEGACY,
        null,
        NewlineTagHandler()
    ).toString().trim()

    Card(
        modifier = Modifier
            .padding(start = 12.dp, end = 12.dp, top = 8.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (result.tags.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(tagScrollState)
                        .padding(start = 12.dp, end = 36.dp, top = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    result.tags.forEach { tag ->
                        ViewPostTag(tag) {
                            explorerViewModel.search(explorerViewModel.hashtagOption, tag.name)
                            navController.navigate(Screen.Explorer.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                }

                if (tagScrollState.canScrollForward) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(36.dp)
                            .align(Alignment.CenterEnd)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                )
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${
                    String.format(Locale.KOREAN, "%,d", result.post.hit)
                }번 조회      ${result.post.submitted.format(CustomTime.fullDate)}에 작성",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

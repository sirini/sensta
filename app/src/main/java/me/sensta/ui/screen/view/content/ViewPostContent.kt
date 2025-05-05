package me.sensta.ui.screen.view.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import com.google.accompanist.flowlayout.FlowRow
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
    val text = result.post.content.parseAsHtml(
        HtmlCompat.FROM_HTML_MODE_LEGACY,
        null,
        NewlineTagHandler()
    ).toString().trim()

    Card(
        modifier = Modifier
            .padding(start = 12.dp, end = 12.dp, top = 8.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 16.dp
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
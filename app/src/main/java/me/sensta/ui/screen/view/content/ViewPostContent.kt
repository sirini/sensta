package me.sensta.ui.screen.view.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import me.sensta.util.NewlineTagHandler
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ViewPostContent(result: TsboardBoardViewResult) {
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
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp
        ) {
            for (tag in result.tags) {
                ViewPostTag(tag)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            val dateFormatter = DateTimeFormatter.ofPattern("yy년 MM월 dd일 HH시 mm분")

            Text(
                text = "${
                    String.format(Locale.KOREAN, "%,d", result.post.hit)
                }번 열람됨    ${result.post.submitted.format(dateFormatter)}에 작성됨",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
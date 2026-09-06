package me.sensta.ui.screen.user

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink

internal sealed interface ChatMessageSegment {
    data class Plain(val value: String) : ChatMessageSegment
    data class Hashtag(val value: String) : ChatMessageSegment
}

private val hashtagPattern = Regex("(?<![\\p{L}\\p{N}_])#([\\p{L}\\p{N}_]+)")

internal fun splitChatMessage(message: String): List<ChatMessageSegment> {
    val segments = mutableListOf<ChatMessageSegment>()
    var cursor = 0
    hashtagPattern.findAll(message).forEach { match ->
        if (match.range.first > cursor) {
            segments += ChatMessageSegment.Plain(message.substring(cursor, match.range.first))
        }
        segments += ChatMessageSegment.Hashtag(match.groupValues[1])
        cursor = match.range.last + 1
    }
    if (cursor < message.length) segments += ChatMessageSegment.Plain(message.substring(cursor))
    return segments.ifEmpty { listOf(ChatMessageSegment.Plain(message)) }
}

@Composable
fun ChatMessageText(
    message: String,
    onHashtagClick: (String) -> Unit
) {
    val linkColor = MaterialTheme.colorScheme.primary
    val annotated = buildAnnotatedString {
        splitChatMessage(message).forEach { segment ->
            when (segment) {
                is ChatMessageSegment.Plain -> append(segment.value)
                is ChatMessageSegment.Hashtag -> withLink(
                    LinkAnnotation.Clickable(
                        tag = segment.value,
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = linkColor,
                                textDecoration = TextDecoration.Underline
                            )
                        ),
                        linkInteractionListener = { onHashtagClick(segment.value) }
                    )
                ) {
                    append("#${segment.value}")
                }
            }
        }
    }
    Text(text = annotated)
}

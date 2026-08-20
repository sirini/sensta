package me.sensta.ui.screen.home.post

import androidx.compose.runtime.Composable
import me.domain.model.board.TsboardPost

@Composable
fun PostCard(post: TsboardPost) {
    PostCardHeader(writer = post.writer)
    FeedCover(path = post.cover, title = post.title)
    PostCardFooter(post)
}

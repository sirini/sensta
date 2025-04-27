package me.sensta.ui.screen.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.domain.model.board.TsboardBoardViewResponse
import me.sensta.ui.screen.home.post.PostCardHeader
import me.sensta.ui.screen.home.post.PostCarousel
import me.sensta.ui.screen.view.content.ViewPostCenterButtons
import me.sensta.ui.screen.view.content.ViewPostContent
import me.sensta.ui.screen.view.content.ViewPostExifDescription
import me.sensta.ui.screen.view.content.ViewPostLikeButton

@Composable
fun ViewPost(
    postView: TsboardBoardViewResponse
) {
    Text(
        text = postView.result.post.title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
    )
    PostCardHeader(writer = postView.result.post.writer)

    if (postView.result.images.isNotEmpty()) {
        PostCarousel(images = postView.result.images)
        ViewPostExifDescription(images = postView.result.images)
    }

    ViewPostLikeButton(post = postView.result.post)
    ViewPostContent(result = postView.result)
    ViewPostCenterButtons(postUid = postView.result.post.uid)
}
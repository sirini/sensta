package me.sensta.ui.screen.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import me.domain.model.board.NuboBoardViewResponse
import me.sensta.ui.screen.home.post.PostCardHeader
import me.sensta.ui.screen.home.post.PostCarousel
import me.sensta.ui.screen.view.content.ViewPostCenterButtons
import me.sensta.ui.screen.view.content.ViewPostContent
import me.sensta.ui.screen.view.content.ViewPostExifDescription
import me.sensta.ui.screen.view.content.ViewPostLikeButton
import me.sensta.util.sharePost

@Composable
fun ViewPost(
    postView: NuboBoardViewResponse
) {
    val context = LocalContext.current
    val post = postView.result.post

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = post.title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        )
        IconButton(onClick = { sharePost(context, post.uid, post.title) }) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "게시물 공유"
            )
        }
    }
    PostCardHeader(writer = postView.result.post.writer)

    if (postView.result.images.isNotEmpty()) {
        PostCarousel(images = postView.result.images, postTitle = post.title)
        ViewPostExifDescription(images = postView.result.images)
    }

    ViewPostLikeButton(post = postView.result.post)
    ViewPostContent(result = postView.result)
    ViewPostCenterButtons(postUid = postView.result.post.uid)
}

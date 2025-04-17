package me.sensta.ui.screen.view.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import me.domain.model.board.TsboardPost
import me.sensta.viewmodel.local.LocalHomeViewModel

@Composable
fun ViewPostLikeButton(post: TsboardPost) {
    val context = LocalContext.current
    val homeViewModel = LocalHomeViewModel.current

    var likeState by remember { mutableStateOf(post.liked) }
    var likeCount by remember { mutableIntStateOf(post.like) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = {
            likeState = !likeState
            homeViewModel.like(post.uid, likeState, context)

            if (likeState) {
                likeCount++
            } else {
                likeCount--
            }
        }) {
            if (likeState) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "like",
                    modifier = Modifier
                        .size(40.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            } else {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "like",
                    modifier = Modifier
                        .size(40.dp)
                )
            }
        }
        Text(text = "${likeCount}개 좋아요", style = MaterialTheme.typography.bodySmall)
    }
}
package me.sensta.ui.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.data.env.Env
import me.domain.model.board.TsboardPost
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.theme.LocalSenstaExtendedColors
import me.sensta.viewmodel.local.LocalCommonViewModel

@Composable
fun UserPhotoGrid(posts: List<TsboardPost>) {
    val navController = LocalNavController.current
    val commonViewModel = LocalCommonViewModel.current
    val onMedia = LocalSenstaExtendedColors.current.onMedia

    if (posts.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "아직 공개한 사진이 없습니다.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(posts, key = { it.uid }) { post ->
            Box(
                modifier = Modifier
                    .aspectRatio(0.8f)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        commonViewModel.updatePostUid(post.uid)
                        navController.navigate(Screen.View.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
            ) {
                AsyncImage(
                    model = Env.DOMAIN + post.cover,
                    contentDescription = post.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                            )
                        )
                        .padding(start = 10.dp, end = 10.dp, top = 28.dp, bottom = 10.dp)
                ) {
                    Text(
                        text = post.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = onMedia,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "좋아요 ${post.like} · 댓글 ${post.comment}",
                        style = MaterialTheme.typography.labelSmall,
                        color = onMedia.copy(alpha = 0.76f)
                    )
                }
            }
        }
    }
}

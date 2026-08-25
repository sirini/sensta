package me.sensta.ui.screen.home.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import me.data.env.Env
import me.domain.model.board.NuboPost
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.viewmodel.local.LocalCommonViewModel
import me.sensta.ui.theme.LocalSenstaExtendedColors
import me.sensta.ui.theme.oleoScriptFontFamily
import me.sensta.util.toPreviewImagePath

@Composable
fun PostCard(post: NuboPost) {
    val navController = LocalNavController.current
    val commonViewModel = LocalCommonViewModel.current
    val onMedia = LocalSenstaExtendedColors.current.onMedia
    val moveToView: () -> Unit = {
        commonViewModel.updatePostUid(post.uid)
        navController.navigate(Screen.View.route) {
            launchSingleTop = true
            restoreState = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FeedCover(
            path = post.cover.toPreviewImagePath(),
            title = post.title,
            onClick = moveToView
        )

        Text(
            text = Env.TITLE.uppercase(),
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = oleoScriptFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 0.sp
            ),
            color = onMedia.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 18.dp, top = 14.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.46f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.88f))
                    )
                )
        )

        PostCardFooter(
            post = post,
            onViewClick = moveToView,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

package me.sensta.ui.screen.home.post

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.data.env.Env
import me.domain.model.board.NuboPost
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.theme.LocalSenstaExtendedColors
import me.sensta.ui.theme.oleoScriptFontFamily
import me.sensta.util.toPreviewImagePath
import me.sensta.viewmodel.local.LocalCommonViewModel
import me.sensta.wallpaper.WallpaperSetResult
import me.sensta.wallpaper.WallpaperSetter
import me.sensta.wallpaper.WallpaperTarget

@Composable
fun PostCard(post: NuboPost) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val commonViewModel = LocalCommonViewModel.current
    val onMedia = LocalSenstaExtendedColors.current.onMedia
    val coroutineScope = rememberCoroutineScope()
    val imagePath = remember(post.cover) { post.cover.toPreviewImagePath() }
    val topScrimHeight = WindowInsets.statusBars
        .asPaddingValues()
        .calculateTopPadding() + 56.dp
    var showWallpaperDialog by remember(post.uid) { mutableStateOf(false) }
    var applyingWallpaperTarget by remember(post.uid) {
        mutableStateOf<WallpaperTarget?>(null)
    }
    val moveToView: () -> Unit = {
        commonViewModel.updatePostUid(post.uid)
        navController.navigate(Screen.View.route) {
            launchSingleTop = true
            restoreState = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FeedCover(
            path = imagePath,
            title = post.title,
            onClick = moveToView,
            onLongClick = { showWallpaperDialog = true }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(topScrimHeight)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color.Black.copy(alpha = 0.42f),
                            0.42f to Color.Black.copy(alpha = 0.24f),
                            0.72f to Color.Black.copy(alpha = 0.08f),
                            1f to Color.Transparent
                        )
                    )
                )
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
                .statusBarsPadding()
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

    if (showWallpaperDialog) {
        WallpaperConfirmationDialog(
            applyingTarget = applyingWallpaperTarget,
            onDismissRequest = {
                if (applyingWallpaperTarget == null) showWallpaperDialog = false
            },
            onApply = { target ->
                if (applyingWallpaperTarget == null) {
                    applyingWallpaperTarget = target
                    coroutineScope.launch {
                        val result = WallpaperSetter.set(
                            context = context,
                            imageUrl = Env.DOMAIN + imagePath,
                            target = target
                        )
                        applyingWallpaperTarget = null
                        showWallpaperDialog = false
                        Toast.makeText(
                            context,
                            result.userMessage(target),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        )
    }
}

private fun WallpaperSetResult.userMessage(target: WallpaperTarget): String = when (this) {
    WallpaperSetResult.SUCCESS -> when (target) {
        WallpaperTarget.HOME_SCREEN -> "홈 화면 배경화면으로 설정했습니다."
        WallpaperTarget.LOCK_SCREEN -> "잠금 화면 배경화면으로 설정했습니다."
        WallpaperTarget.BOTH -> "홈 화면과 잠금 화면 배경화면으로 설정했습니다."
    }
    WallpaperSetResult.NOT_SUPPORTED -> "이 기기에서는 배경화면 설정을 지원하지 않습니다."
    WallpaperSetResult.NOT_ALLOWED -> "기기 설정에서 배경화면 변경이 허용되지 않았습니다."
    WallpaperSetResult.IMAGE_LOAD_FAILED -> "사진을 불러오지 못했습니다. 잠시 후 다시 시도해주세요."
    WallpaperSetResult.FAILED -> "배경화면을 설정하지 못했습니다. 잠시 후 다시 시도해주세요."
}

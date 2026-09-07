package me.sensta.ui.screen.upload

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.local.LocalUploadViewModel

@Composable
fun UploadCompleted() {
    val context = LocalContext.current
    val authViewModel = LocalAuthViewModel.current
    val uploadViewModel = LocalUploadViewModel.current
    val isLoading by uploadViewModel.isLoading
    val uploadedPostUid by uploadViewModel.uploadedPostUid

    LaunchedEffect(Unit) {
        uploadViewModel.beginUploadPreparation()
        if (authViewModel.refreshForProtectedRequest()) {
            uploadViewModel.upload(context)
        } else {
            uploadViewModel.failUploadPreparation("로그인 세션이 만료되었습니다")
        }
    }

    AnimatedContent(
        targetState = isLoading,
        transitionSpec = {
            slideInHorizontally(
                animationSpec = tween(300),
                initialOffsetX = { width -> width }
            ) togetherWith slideOutHorizontally(
                animationSpec = tween(300),
                targetOffsetX = { width -> -width }
            )
        },
        label = "UploadingTransition"
    ) { currentLoadingState ->
        when (currentLoadingState) {
            true -> Uploading()
            false -> {
                if (uploadedPostUid > 0) {
                    UploadingDone()
                } else {
                    UploadingFailed()
                }
            }
        }
    }
}

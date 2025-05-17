package me.sensta.ui.screen

import android.text.format.Formatter
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import me.data.env.Env
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.screen.upload.UploadCompleted
import me.sensta.ui.screen.upload.UploadInputContent
import me.sensta.ui.screen.upload.UploadInputTag
import me.sensta.ui.screen.upload.UploadInputTitle
import me.sensta.ui.screen.upload.UploadSelectImages
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.local.LocalUploadViewModel
import me.sensta.viewmodel.state.UploadState
import me.sensta.viewmodel.uievent.UploadUiEvent

@Composable
fun UploadScreen() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val imeInsets = WindowInsets.ime
    val density = LocalDensity.current
    val authViewModel = LocalAuthViewModel.current
    val uploadViewModel = LocalUploadViewModel.current
    val isLoading by uploadViewModel.isLoading
    val pickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(Env.MAX_UPLOAD_COUNT)
    ) { uris -> uploadViewModel.setUris(uris, context) }
    val uploadState by uploadViewModel.uploadState

    LaunchedEffect(Unit) {
        // 로그인 안했으면 로그인 화면으로 이동
        if (authViewModel.user.value.token.isEmpty()) {
            navController.navigate(Screen.Login.route) {
                launchSingleTop = true
                restoreState = true
            }
            return@LaunchedEffect
        }

        // 이전에 업로드 했던 uris 비우기
        uploadViewModel.clearPreviousUpload()

        // 로그인 했으면 이미지 선택하는 런처 실행
        pickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )

        // ViewModel로부터 온 이벤트 수신하고 메시지 띄우기
        uploadViewModel.uiEvent.collect { event ->
            when (event) {
                is UploadUiEvent.FileSizeExceeded -> {
                    val current = Formatter.formatFileSize(context, event.current)
                    val limit = Formatter.formatFileSize(context, event.limit)
                    Toast.makeText(
                        context,
                        "선택하신 사진(들)의 크기가 너무 큽니다 (${current} / $limit",
                        Toast.LENGTH_LONG
                    ).show()
                }

                is UploadUiEvent.InvalidHashtag -> {
                    Toast.makeText(context, "올바르지 않은 해시태그 형식입니다", Toast.LENGTH_SHORT).show()
                }

                is UploadUiEvent.AlreadyAddedHashtag -> {
                    Toast.makeText(context, "이미 추가된 해시태그입니다", Toast.LENGTH_SHORT).show()
                }

                is UploadUiEvent.HashtagRemoved -> {
                    Toast.makeText(context, "해시태그 ${event.tag}(을)를 삭제하였습니다", Toast.LENGTH_SHORT)
                        .show()
                }

                is UploadUiEvent.PostUploaded -> {
                    Toast.makeText(context, "게시글 업로드가 완료되었습니다", Toast.LENGTH_SHORT).show()
                }

                is UploadUiEvent.FailedToUpload -> {
                    Toast.makeText(
                        context,
                        "게시글을 업로드하지 못했습니다 (${event.message})",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // 가상 키보드 높이 절반 계산
    val halfKeyboardPadding by remember {
        derivedStateOf {
            with(density) {
                (imeInsets.getBottom(this) / 2).toDp()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = halfKeyboardPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Column {
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                    )
                }

                AnimatedContent(
                    targetState = uploadState,
                    transitionSpec = {
                        slideInHorizontally(
                            animationSpec = tween(300),
                            initialOffsetX = { width -> width }
                        ) togetherWith slideOutHorizontally(
                            animationSpec = tween(300),
                            targetOffsetX = { width -> -width }
                        )
                    },
                    label = "UploadTransition"
                ) { currentUploadState ->
                    when (currentUploadState) {
                        UploadState.SelectImage -> UploadSelectImages()
                        UploadState.InputTitle -> UploadInputTitle()
                        UploadState.InputContent -> UploadInputContent()
                        UploadState.InputTag -> UploadInputTag()
                        UploadState.UploadCompleted -> UploadCompleted()
                    }
                }
            }
        }
    }
}
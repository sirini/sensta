package me.sensta.ui.screen

import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import me.domain.repository.NuboResponse
import me.sensta.ui.common.LocalScrollBehavior
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.navigation.common.LocalSnackbar
import me.sensta.ui.screen.view.ViewPost
import me.sensta.ui.screen.view.comment.CommentCard
import me.sensta.viewmodel.local.LocalCommentViewModel
import me.sensta.viewmodel.local.LocalCommonViewModel
import me.sensta.viewmodel.local.LocalPostViewViewModel
import me.sensta.viewmodel.local.LocalHomeViewModel
import me.sensta.viewmodel.uievent.ViewUiEvent
import me.sensta.viewmodel.uievent.HomeUiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewScreen(initialPostUid: Int = 0) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val postViewViewModel = LocalPostViewViewModel.current
    val commentViewModel = LocalCommentViewModel.current
    val scrollBehavior = LocalScrollBehavior.current
    val commonViewModel = LocalCommonViewModel.current
    val homeViewModel = LocalHomeViewModel.current
    val snackbar = LocalSnackbar.current
    val postUid by commonViewModel.postUid
    val post by postViewViewModel.post
    val comments by commentViewModel.comments
    val requestedPostUid = initialPostUid.takeIf { it > 0 } ?: postUid

    LaunchedEffect(requestedPostUid) {
        // 스크롤 상태를 초기화해서 topBar가 펼쳐진 상태로 만들기
        scrollBehavior.state.heightOffset = 0f

        // 게시글 및 댓글 가져오기
        commonViewModel.updatePostUid(requestedPostUid)
        postViewViewModel.refresh(postUid = requestedPostUid)
        commentViewModel.refresh(postUid = requestedPostUid)
    }

    LaunchedEffect(Unit) {
        launch {
            homeViewModel.uiEvent.collect { event ->
                when (event) {
                    is HomeUiEvent.LikePost -> Toast.makeText(
                        context,
                        "게시글에 좋아요를 남겼습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    is HomeUiEvent.CancelLikePost -> Toast.makeText(
                        context,
                        "좋아요를 취소했습니다",
                        Toast.LENGTH_SHORT
                    ).show()
                    is HomeUiEvent.FailedToUpdateLike -> snackbar.showSnackbar(
                        "좋아요 변경에 실패했습니다 (${event.message})",
                        "확인",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }

        // PostViewViewModel에서 전달된 이벤트들에 따라 메시지 출력하기
        launch {
            postViewViewModel.uiEvent.collect { event ->
                when (event) {
                    is ViewUiEvent.PostRemoved -> {
                        snackbar.showSnackbar(
                            "게시글을 삭제하였습니다",
                            "확인",
                            duration = SnackbarDuration.Short
                        )
                        navController.navigate(Screen.Home.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }

                    is ViewUiEvent.FailedToRemovePost -> {
                        snackbar.showSnackbar(
                            "게시글 삭제에 실패했습니다 (${event.message})",
                            "확인",
                            duration = SnackbarDuration.Short
                        )
                    }

                    is ViewUiEvent.PostEdited -> {
                        snackbar.showSnackbar(
                            "게시글을 수정했습니다",
                            duration = SnackbarDuration.Short
                        )
                    }

                    is ViewUiEvent.FailedToEditPost -> {
                        snackbar.showSnackbar(
                            "게시글 수정에 실패했습니다 (${event.message})",
                            "확인",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }

    // 게시글 가져오기 성공 시에만 내용 표시
    when (val postViewResponse = post) {
        is NuboResponse.Loading -> LoadingScreen()
        is NuboResponse.Success -> {
            val postView = postViewResponse.data
            val commentResponse = comments

            if (commentResponse is NuboResponse.Success) {
                val commentList = commentResponse.data
                LazyColumn {
                    item { ViewPost(postView) }
                    items(commentList) { CommentCard(it) }
                }
            }
        }

        is NuboResponse.Error -> ErrorScreen()
    }
}

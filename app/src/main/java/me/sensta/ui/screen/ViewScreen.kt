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
import me.domain.repository.TsboardResponse
import me.sensta.ui.common.LocalScrollBehavior
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.navigation.common.LocalSnackbar
import me.sensta.ui.screen.view.ViewPost
import me.sensta.ui.screen.view.comment.CommentCard
import me.sensta.viewmodel.local.LocalCommentViewModel
import me.sensta.viewmodel.local.LocalCommonViewModel
import me.sensta.viewmodel.local.LocalPostViewViewModel
import me.sensta.viewmodel.uievent.CommentUiEvent
import me.sensta.viewmodel.uievent.ViewUiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewScreen() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val postViewViewModel = LocalPostViewViewModel.current
    val commentViewModel = LocalCommentViewModel.current
    val scrollBehavior = LocalScrollBehavior.current
    val commonViewModel = LocalCommonViewModel.current
    val snackbar = LocalSnackbar.current
    val postUid by commonViewModel.postUid
    val post by postViewViewModel.post
    val comments by commentViewModel.comments

    LaunchedEffect(Unit) {
        // 스크롤 상태를 초기화해서 topBar가 펼쳐진 상태로 만들기
        scrollBehavior.state.heightOffset = 0f

        // 게시글 및 댓글 가져오기
        postViewViewModel.refresh(postUid = postUid)
        commentViewModel.refresh(postUid = postUid)

        // CommentViewModel에서 전달된 이벤트들에 따라 메시지 출력하기
        launch {
            commentViewModel.uiEvent.collect { event ->
                when (event) {
                    is CommentUiEvent.CancelLikeComment -> {
                        Toast.makeText(context, "좋아요를 취소했습니다", Toast.LENGTH_SHORT).show()
                    }

                    is CommentUiEvent.FailedToRemoveComment -> {
                        snackbar.showSnackbar(
                            "댓글 삭제에 실패했습니다 (${event.message})",
                            "확인",
                            duration = SnackbarDuration.Short
                        )
                    }

                    is CommentUiEvent.LikeComment -> {
                        Toast.makeText(context, "댓글에 좋아요를 남겼습니다", Toast.LENGTH_SHORT).show()
                    }

                    is CommentUiEvent.CommentRemoved -> {
                        Toast.makeText(context, "댓글이 삭제되었습니다", Toast.LENGTH_SHORT).show()
                        commentViewModel.refresh(postUid = postUid)
                    }

                    is CommentUiEvent.WroteComment -> {
                        Toast.makeText(context, "댓글을 작성했습니다", Toast.LENGTH_SHORT).show()
                        commentViewModel.refresh(postUid = postUid)
                    }

                    is CommentUiEvent.FailedToWriteComment -> {
                        snackbar.showSnackbar(
                            "댓글 작성에 실패했습니다 (${event.message})",
                            "확인",
                            duration = SnackbarDuration.Short
                        )
                    }
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
                }
            }
        }
    }

    // 게시글 가져오기 성공 시에만 내용 표시
    when (val postViewResponse = post) {
        is TsboardResponse.Loading -> LoadingScreen()
        is TsboardResponse.Success -> {
            val postView = postViewResponse.data
            val commentResponse = comments

            if (commentResponse is TsboardResponse.Success) {
                val commentList = commentResponse.data
                LazyColumn {
                    item { ViewPost(postView) }
                    items(commentList) { CommentCard(it) }
                }
            }
        }

        is TsboardResponse.Error -> ErrorScreen()
    }
}
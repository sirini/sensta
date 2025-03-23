package me.sensta.ui.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import me.domain.model.board.TsboardBoardViewResponse
import me.domain.model.board.TsboardComment
import me.domain.repository.TsboardResponse
import me.sensta.ui.common.LocalScrollBehavior
import me.sensta.ui.screen.home.Loading
import me.sensta.ui.screen.home.comment.CommentCard
import me.sensta.ui.screen.view.ViewPost
import me.sensta.viewmodel.CommentViewModel
import me.sensta.viewmodel.PostViewViewModel
import me.sensta.viewmodel.common.LocalCommonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewScreen() {
    val postViewViewModel: PostViewViewModel = hiltViewModel()
    val postViewResponse by postViewViewModel.post.collectAsState()

    val scrollBehavior = LocalScrollBehavior.current
    val commonViewModel = LocalCommonViewModel.current
    val postUid by commonViewModel.postUid

    val commentViewModel: CommentViewModel = hiltViewModel()
    val commentResponse by commentViewModel.comments.collectAsState()

    // 스크롤 상태를 초기화해서 topBar가 펼쳐진 상태로 만들기
    LaunchedEffect(Unit) {
        scrollBehavior.state.heightOffset = 0f

        // 게시글 및 댓글 가져오기
        postViewViewModel.refresh(postUid = postUid)
        commentViewModel.refresh(postUid = postUid)
    }

    // 게시글 가져오기 성공 시에만 내용 표시
    when (postViewResponse) {
        is TsboardResponse.Loading -> {
            Loading()
        }

        is TsboardResponse.Success -> {
            val postView =
                (postViewResponse as TsboardResponse.Success<TsboardBoardViewResponse>).data

            if (commentResponse is TsboardResponse.Success) {
                val comments =
                    (commentResponse as TsboardResponse.Success<List<TsboardComment>>).data

                LazyColumn {
                    item { ViewPost(postView) }
                    items(comments) { CommentCard(it) }
                }
            }
        }

        is TsboardResponse.Error -> {
            ErrorScreen()
        }
    }
}
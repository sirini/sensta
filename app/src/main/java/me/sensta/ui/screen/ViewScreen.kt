package me.sensta.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import me.domain.repository.TsboardResponse
import me.sensta.ui.screen.home.Loading
import me.sensta.ui.screen.view.ViewPostComment
import me.sensta.viewmodel.CommentViewModel
import me.sensta.viewmodel.common.LocalCommonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewScreen(scrollBehavior: TopAppBarScrollBehavior) {
    val commentViewModel: CommentViewModel = hiltViewModel()
    val commentResponse by commentViewModel.comments.collectAsState()
    val commonViewModel = LocalCommonViewModel.current
    val photo by commonViewModel.photo

    // 댓글 목록 가져오기
    commentViewModel.refresh(postUid = photo.uid)

    // 스크롤 상태를 초기화해서 topBar가 펼쳐진 상태로 만들기
    LaunchedEffect(Unit) {
        scrollBehavior.state.heightOffset = 0f
    }

    // 댓글 가져오기 성공 시에만 댓글 목록 표시
    when (commentResponse) {
        is TsboardResponse.Loading -> {
            Loading()
        }

        is TsboardResponse.Success -> {
            ViewPostComment(commentResponse = commentResponse)
        }

        is TsboardResponse.Error -> {
            ErrorScreen()
        }
    }
}
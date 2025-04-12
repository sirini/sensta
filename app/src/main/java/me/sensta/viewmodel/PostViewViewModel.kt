package me.sensta.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.domain.model.board.TsboardBoardViewResponse
import me.domain.repository.TsboardResponse
import me.domain.usecase.view.GetPostViewUseCase
import javax.inject.Inject

@HiltViewModel
class PostViewViewModel @Inject constructor(
    private val getPostViewUseCase: GetPostViewUseCase
) : ViewModel() {
    private var _post =
        mutableStateOf<TsboardResponse<TsboardBoardViewResponse>>(TsboardResponse.Loading)
    val post: State<TsboardResponse<TsboardBoardViewResponse>> get() = _post

    // 게시글 내용 가져오기
    private fun loadPostView(postUid: Int) {
        viewModelScope.launch {
            getPostViewUseCase(postUid = postUid).collect {
                _post.value = it
            }
        }
    }

    // 게시글 내용 업데이트
    fun refresh(postUid: Int) = loadPostView(postUid = postUid)
}
package me.sensta.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.domain.model.board.TsboardComment
import me.domain.repository.TsboardResponse
import me.domain.usecase.GetCommentListUseCase
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val getCommentListUseCase: GetCommentListUseCase
) : ViewModel() {
    private val _comments =
        MutableStateFlow<TsboardResponse<List<TsboardComment>>>(TsboardResponse.Loading)
    val comments = _comments.asStateFlow()

    // 댓글 목록 가져오기
    private fun loadComments(postUid: Int) {
        viewModelScope.launch {
            getCommentListUseCase(postUid = postUid).collect {
                _comments.value = it
            }
        }
    }

    // 댓글 목록 업데이트
    fun refresh(postUid: Int) {
        loadComments(postUid = postUid)
    }
}
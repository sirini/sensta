package me.sensta.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.data.env.Env
import me.domain.model.board.TsboardComment
import me.domain.repository.TsboardResponse
import me.domain.repository.handle
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.board.RemoveCommentUseCase
import me.domain.usecase.board.UpdateLikeCommentUseCase
import me.domain.usecase.board.WriteCommentUseCase
import me.domain.usecase.view.GetCommentListUseCase
import me.sensta.viewmodel.uievent.CommentUiEvent
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getCommentListUseCase: GetCommentListUseCase,
    private val removeCommentUseCase: RemoveCommentUseCase,
    private val updateLikeCommentUseCase: UpdateLikeCommentUseCase,
    private val writeCommentUseCase: WriteCommentUseCase
) : ViewModel() {
    private val _comments =
        mutableStateOf<TsboardResponse<List<TsboardComment>>>(TsboardResponse.Loading)
    val comments: State<TsboardResponse<List<TsboardComment>>> get() = _comments

    private val _uiEvent = MutableSharedFlow<CommentUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // 댓글 목록 가져오기
    private fun loadComments(postUid: Int) {
        viewModelScope.launch {
            val token = getUserInfoUseCase().first().token
            getCommentListUseCase(postUid = postUid, token = token).collect {
                _comments.value = it
            }
        }
    }

    // 댓글에 좋아요 클릭하기
    fun like(commentUid: Int, liked: Boolean) {
        viewModelScope.launch {
            val token = getUserInfoUseCase().first().token
            if (token.isEmpty()) return@launch

            updateLikeCommentUseCase(
                boardUid = Env.BOARD_UID,
                commentUid = commentUid,
                liked = liked,
                token = token
            ).collect { result ->
                result.handle { resp ->
                    if (resp.success) {
                        if (liked) {
                            _uiEvent.emit(CommentUiEvent.LikeComment)
                        } else {
                            _uiEvent.emit(CommentUiEvent.CancelLikeComment)
                        }
                    }
                }
            }
        }
    }

    // 댓글 목록 업데이트
    fun refresh(postUid: Int) {
        loadComments(postUid = postUid)
    }

    // 내가 작성한 댓글 삭제하기
    fun remove(removeTargetUid: Int, postUid: Int) {
        viewModelScope.launch {
            val token = getUserInfoUseCase().first().token
            if (token.isEmpty()) return@launch

            removeCommentUseCase(
                boardUid = Env.BOARD_UID,
                removeTargetUid = removeTargetUid,
                token = token
            ).collect { result ->
                result.handle { resp ->
                    if (resp.success) {
                        refresh(postUid)
                        _uiEvent.emit(CommentUiEvent.CommentRemoved)
                    } else {
                        _uiEvent.emit(CommentUiEvent.FailedToRemoveComment(resp.error))
                    }
                }
            }
        }
    }

    // 댓글 작성하기
    fun write(postUid: Int, content: String) {
        viewModelScope.launch {
            val token = getUserInfoUseCase().first().token
            if (token.isEmpty()) return@launch

            writeCommentUseCase(
                boardUid = Env.BOARD_UID,
                postUid = postUid,
                content = content,
                token = token
            ).collect { result ->
                result.handle { resp ->
                    if (resp.success) {
                        _uiEvent.emit(CommentUiEvent.WroteComment)
                    } else {
                        _uiEvent.emit(CommentUiEvent.FailedToWriteComment(resp.error))
                    }
                }
            }
        }
    }
}
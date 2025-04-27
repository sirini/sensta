package me.sensta.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.data.env.Env
import me.domain.model.board.TsboardComment
import me.domain.model.board.TsboardCommentWriteResponse
import me.domain.model.common.TsboardResponseNothing
import me.domain.repository.TsboardResponse
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.board.RemoveCommentUseCase
import me.domain.usecase.board.UpdateLikeCommentUseCase
import me.domain.usecase.board.WriteCommentUseCase
import me.domain.usecase.view.GetCommentListUseCase
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

    // 댓글 목록 가져오기
    private fun loadComments(postUid: Int) {
        viewModelScope.launch {

            var token = ""
            getUserInfoUseCase().collect {
                token = it.token
            }

            getCommentListUseCase(postUid = postUid, token = token).collect {
                _comments.value = it
            }
        }
    }

    // 댓글에 좋아요 클릭하기
    fun like(commentUid: Int, liked: Boolean, context: Context? = null) {
        viewModelScope.launch {
            getUserInfoUseCase().collect {
                if (it.token.isEmpty()) return@collect

                updateLikeCommentUseCase(
                    boardUid = Env.boardUid,
                    commentUid = commentUid,
                    liked = liked,
                    token = it.token
                ).collect { result ->
                    val data = (result as TsboardResponse.Success<TsboardResponseNothing>).data
                    if (data.success && null != context) {
                        if (liked) {
                            Toast.makeText(context, "이 댓글에 좋아요를 남겼습니다", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "이 댓글에 좋아요를 취소했습니다", Toast.LENGTH_SHORT).show()
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
    fun remove(removeTargetUid: Int, postUid: Int, context: Context) {
        viewModelScope.launch {
            getUserInfoUseCase().collect {
                if (it.token.isEmpty()) return@collect

                removeCommentUseCase(
                    boardUid = Env.boardUid,
                    removeTargetUid = removeTargetUid,
                    token = it.token
                ).collect { result ->
                    val data = (result as TsboardResponse.Success<TsboardResponseNothing>).data
                    if (data.success) {
                        refresh(postUid)
                        Toast.makeText(context, "댓글을 삭제했습니다", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "댓글 삭제에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // 댓글 작성하기
    fun write(postUid: Int, content: String, context: Context) {
        viewModelScope.launch {
            getUserInfoUseCase().collect {
                if (it.token.isEmpty()) return@collect

                writeCommentUseCase(
                    boardUid = Env.boardUid,
                    postUid = postUid,
                    content = content,
                    token = it.token
                ).collect { result ->
                    val data = (result as TsboardResponse.Success<TsboardCommentWriteResponse>).data
                    if (data.success) {
                        Toast.makeText(context, "댓글을 작성했습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
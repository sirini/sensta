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
import me.domain.model.board.TsboardBoardViewResponse
import me.domain.repository.TsboardResponse
import me.domain.repository.handle
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.board.RemovePostUseCase
import me.domain.usecase.view.GetPostViewUseCase
import me.sensta.viewmodel.uievent.ViewUiEvent
import javax.inject.Inject

@HiltViewModel
class PostViewViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getPostViewUseCase: GetPostViewUseCase,
    private val removePostUseCase: RemovePostUseCase
) : ViewModel() {
    private var _post =
        mutableStateOf<TsboardResponse<TsboardBoardViewResponse>>(TsboardResponse.Loading)
    val post: State<TsboardResponse<TsboardBoardViewResponse>> get() = _post

    private val _openedPosts = mutableListOf<Int>()
    private val _uiEvent = MutableSharedFlow<ViewUiEvent>()
    val uiEvent get() = _uiEvent.asSharedFlow()

    // 게시글 내용 가져오기
    private fun loadPostView(postUid: Int) {
        viewModelScope.launch {
            // 이미 열람한 적 있으면 조회수 올리지 않기
            var needUpdateHit = true
            if (_openedPosts.contains(postUid)) {
                needUpdateHit = false
            } else {
                _openedPosts.add(postUid)
            }

            val token = getUserInfoUseCase().first().token
            getPostViewUseCase(
                postUid = postUid,
                token = token,
                needUpdateHit = needUpdateHit
            ).collect {
                _post.value = it
            }
        }
    }

    // 게시글 내용 업데이트
    fun refresh(postUid: Int) = loadPostView(postUid = postUid)

    // 게시글 삭제하기
    fun remove(postUid: Int) {
        viewModelScope.launch {
            val token = getUserInfoUseCase().first().token
            if (token.isEmpty()) return@launch

            removePostUseCase(
                boardUid = Env.BOARD_UID,
                postUid = postUid,
                token = token
            ).collect {
                it.handle { resp ->
                    if (resp.success) {
                        _uiEvent.emit(ViewUiEvent.PostRemoved)
                    } else {
                        _uiEvent.emit(ViewUiEvent.FailedToRemovePost(resp.error))
                    }
                }
            }
        }
    }
}
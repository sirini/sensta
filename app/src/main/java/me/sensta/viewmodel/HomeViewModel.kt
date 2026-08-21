package me.sensta.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.data.env.Env
import me.domain.model.board.TsboardPost
import me.domain.repository.TsboardResponse
import me.domain.repository.handle
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.board.GetPostListUseCase
import me.domain.usecase.board.UpdateLikePostUseCase
import me.sensta.viewmodel.uievent.HomeUiEvent
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPostListUseCase: GetPostListUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val updateLikePostUseCase: UpdateLikePostUseCase
) : ViewModel() {
    private val _posts =
        mutableStateOf<TsboardResponse<List<TsboardPost>>>(TsboardResponse.Loading)
    val posts: State<TsboardResponse<List<TsboardPost>>> get() = _posts

    private val _isLoadingMore = mutableStateOf(false)
    val isLoadingMore: State<Boolean> get() = _isLoadingMore

    private val _page = mutableIntStateOf(1)
    val page: State<Int> get() = _page

    private val _bunch = mutableIntStateOf(0)
    val bunch: State<Int> get() = _bunch

    private val _feedIndex = mutableIntStateOf(0)
    val feedIndex: State<Int> get() = _feedIndex

    private val _uiEvent = MutableSharedFlow<HomeUiEvent>()
    val uiEvent get() = _uiEvent.asSharedFlow()
    private var loadedForUserUid: Int? = null
    private var pendingUserUid: Int? = null

    init {
        loadPhotos()
    }

    // 갤러리 사진 목록 가져오기
    private fun loadPhotos() {
        if (_isLoadingMore.value) return

        viewModelScope.launch {
            if (_page.intValue == 1) {
                _posts.value = TsboardResponse.Loading
                _page.intValue = 1
            }
            _isLoadingMore.value = true

            val token = getUserInfoUseCase().first().token
            getPostListUseCase(
                page = _page.intValue,
                option = 0,
                keyword = "",
                token = token
            ).collect {
                it.handle { resp ->
                    if (_page.intValue == 1) {
                        _posts.value = TsboardResponse.Success(resp)
                        _bunch.intValue = resp.size
                    } else {
                        // 이전 게시글들을 이어서 붙여나가기
                        val currentPosts =
                            (_posts.value as TsboardResponse.Success<List<TsboardPost>>).data
                        resp.ifEmpty {
                            _posts.value = TsboardResponse.Success(currentPosts)
                            return@handle
                        }
                        _posts.value = TsboardResponse.Success(currentPosts + resp)
                    }
                    if (resp.isNotEmpty()) _page.intValue++
                }
            }
            _isLoadingMore.value = false
            pendingUserUid?.let { userUid ->
                pendingUserUid = null
                refreshForUser(userUid)
            }
        }
    }

    // 갤러리 목록 업데이트
    fun refresh(resetPaging: Boolean = false) {
        if (resetPaging) _page.intValue = 1
        loadPhotos()
    }

    fun refreshForUser(userUid: Int) {
        if (loadedForUserUid == userUid) return
        if (_isLoadingMore.value) {
            pendingUserUid = userUid
            return
        }
        loadedForUserUid = userUid
        refresh(resetPaging = true)
    }

    fun updateFeedIndex(index: Int) {
        _feedIndex.intValue = index.coerceAtLeast(0)
    }

    // 게시글에 좋아요 누르기
    fun like(postUid: Int, liked: Boolean) {
        viewModelScope.launch {
            val token = getUserInfoUseCase().first().token
            if (token.isEmpty()) return@launch

            updateLikePostUseCase(
                boardUid = Env.BOARD_UID,
                postUid = postUid,
                liked = liked,
                token = token
            ).collect { result ->
                result.handle { resp ->
                    if (resp.success) {
                        if (liked) {
                            _uiEvent.emit(HomeUiEvent.LikePost)
                        } else {
                            _uiEvent.emit(HomeUiEvent.CancelLikePost)
                        }
                    }
                }
            }
        }
    }
}

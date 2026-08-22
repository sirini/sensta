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
import me.domain.model.board.NuboPost
import me.domain.repository.NuboResponse
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.board.GetPostListUseCase
import me.domain.usecase.board.UpdateLikePostUseCase
import me.sensta.diagnostics.AppDiagnostics
import me.sensta.viewmodel.uievent.HomeUiEvent
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPostListUseCase: GetPostListUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val updateLikePostUseCase: UpdateLikePostUseCase
) : ViewModel() {
    private val _posts =
        mutableStateOf<NuboResponse<List<NuboPost>>>(NuboResponse.Loading)
    val posts: State<NuboResponse<List<NuboPost>>> get() = _posts

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

    // 첫 화면과 다음 페이지가 같은 흐름을 쓰되 상태 갱신은 작은 함수에 맡긴다.
    private fun loadPhotos() {
        if (_isLoadingMore.value) return

        viewModelScope.launch {
            prepareLoadingState()
            try {
                val token = getUserInfoUseCase().first().token
                getPostListUseCase(
                    page = _page.intValue,
                    option = 0,
                    keyword = "",
                    token = token
                ).collect { response ->
                    when (response) {
                        NuboResponse.Loading -> Unit
                        is NuboResponse.Success -> applyLoadedPosts(response.data)
                        is NuboResponse.Error -> {
                            AppDiagnostics.report("홈 사진 목록", response)
                            _posts.value = response
                        }
                    }
                }
            } finally {
                finishLoading()
            }
        }
    }

    private fun prepareLoadingState() {
        if (_page.intValue == 1) _posts.value = NuboResponse.Loading
        _isLoadingMore.value = true
    }

    private fun applyLoadedPosts(newPosts: List<NuboPost>) {
        if (_page.intValue == 1) {
            _posts.value = NuboResponse.Success(newPosts)
            _bunch.intValue = newPosts.size
        } else {
            val currentPosts = (_posts.value as? NuboResponse.Success)?.data.orEmpty()
            _posts.value = NuboResponse.Success(currentPosts + newPosts)
        }
        if (newPosts.isNotEmpty()) _page.intValue++
    }

    private fun finishLoading() {
        _isLoadingMore.value = false
        pendingUserUid?.let { userUid ->
            pendingUserUid = null
            refreshForUser(userUid)
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
            ).collect { response ->
                when (response) {
                    NuboResponse.Loading -> Unit
                    is NuboResponse.Error -> AppDiagnostics.report("게시글 좋아요", response)
                    is NuboResponse.Success -> {
                        if (response.data.success) {
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
}

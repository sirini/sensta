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
import me.sensta.sync.BoardMutation
import me.sensta.sync.BoardStateSync
import me.sensta.sync.applyPostMutation
import me.sensta.viewmodel.uievent.HomeUiEvent
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPostListUseCase: GetPostListUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val updateLikePostUseCase: UpdateLikePostUseCase,
    private val boardStateSync: BoardStateSync
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
    private val pendingLikePosts = mutableSetOf<Int>()

    init {
        viewModelScope.launch {
            boardStateSync.mutations.collect(::applyMutation)
        }
    }

    // 첫 화면과 다음 페이지가 같은 흐름을 쓰되 상태 갱신은 작은 함수에 맡긴다.
    private fun loadPhotos() {
        if (_isLoadingMore.value) return

        viewModelScope.launch {
            val requestRevision = boardStateSync.revision
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
                        is NuboResponse.Success -> applyLoadedPosts(response.data, requestRevision)
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

    private fun applyLoadedPosts(newPosts: List<NuboPost>, requestRevision: Long) {
        val synchronizedPosts = boardStateSync.applyToPosts(newPosts, requestRevision)
        if (_page.intValue == 1) {
            _posts.value = NuboResponse.Success(synchronizedPosts)
            _bunch.intValue = synchronizedPosts.size
        } else {
            val currentPosts = (_posts.value as? NuboResponse.Success)?.data.orEmpty()
            _posts.value = NuboResponse.Success((currentPosts + synchronizedPosts).distinctBy(NuboPost::uid))
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
    fun like(postUid: Int, liked: Boolean, currentLikeCount: Int? = null) {
        if (!pendingLikePosts.add(postUid)) return
        val currentPost = (_posts.value as? NuboResponse.Success)
            ?.data
            ?.firstOrNull { it.uid == postUid }
        val previousLiked = !liked
        val previousLikeCount = currentLikeCount ?: currentPost?.like
        val expectedLikeCount = previousLikeCount?.let { count ->
            (count + if (liked) 1 else -1).coerceAtLeast(0)
        }
        boardStateSync.changePostLike(postUid, liked, expectedLikeCount)

        viewModelScope.launch {
            try {
                val token = getUserInfoUseCase().first().token
                if (token.isEmpty()) {
                    rollbackLike(postUid, previousLiked, previousLikeCount, "로그인이 필요합니다")
                    return@launch
                }

                updateLikePostUseCase(
                    boardUid = Env.BOARD_UID,
                    postUid = postUid,
                    liked = liked,
                    token = token
                ).collect { response ->
                    when (response) {
                        NuboResponse.Loading -> Unit
                        is NuboResponse.Error -> {
                            AppDiagnostics.report("게시글 좋아요", response)
                            rollbackLike(postUid, previousLiked, previousLikeCount, response.message)
                        }
                        is NuboResponse.Success -> {
                            if (response.data.success) {
                                if (liked) {
                                    _uiEvent.emit(HomeUiEvent.LikePost)
                                } else {
                                    _uiEvent.emit(HomeUiEvent.CancelLikePost)
                                }
                            } else {
                                rollbackLike(postUid, previousLiked, previousLikeCount, response.data.error)
                            }
                        }
                    }
                }
            } finally {
                pendingLikePosts.remove(postUid)
            }
        }
    }

    private fun applyMutation(mutation: BoardMutation) {
        val current = (_posts.value as? NuboResponse.Success)?.data ?: return
        val changed = current.applyPostMutation(mutation)
        _posts.value = NuboResponse.Success(changed)
        if (mutation is BoardMutation.PostRemoved) {
            _feedIndex.intValue = _feedIndex.intValue.coerceAtMost(changed.lastIndex.coerceAtLeast(0))
        }
    }

    private suspend fun rollbackLike(
        postUid: Int,
        liked: Boolean,
        expectedLikeCount: Int?,
        message: String
    ) {
        boardStateSync.changePostLike(postUid, liked, expectedLikeCount)
        _uiEvent.emit(HomeUiEvent.FailedToUpdateLike(message))
    }
}

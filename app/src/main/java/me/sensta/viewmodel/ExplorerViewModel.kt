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
import me.domain.model.board.NuboRecentHashtag
import me.domain.repository.NuboResponse
import me.domain.repository.handle
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.board.GetPostListUseCase
import me.domain.usecase.board.GetRecentHashtagListUseCase
import me.sensta.viewmodel.uievent.ExplorerUiEvent
import me.sensta.sync.BoardMutation
import me.sensta.sync.BoardStateSync
import me.sensta.sync.applyPostMutation
import me.sensta.diagnostics.AppDiagnostics
import javax.inject.Inject

@HiltViewModel
class ExplorerViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getPostListUseCase: GetPostListUseCase,
    private val getRecentHashtagListUseCase: GetRecentHashtagListUseCase,
    private val boardStateSync: BoardStateSync
) : ViewModel() {
    val aiDescOption = 12
    val hashtagOption = 3
    val writerOption = 2
    val titleOption = 0
    val contentOption = 1

    private var _posts =
        mutableStateOf<NuboResponse<List<NuboPost>>>(NuboResponse.Loading)
    val posts: State<NuboResponse<List<NuboPost>>> get() = _posts

    private val _isLoadingMore = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoadingMore

    private val _option = mutableIntStateOf(aiDescOption)
    val option: State<Int> get() = _option

    private val _keyword = mutableStateOf("")
    val keyword: State<String> get() = _keyword

    private val _page = mutableIntStateOf(1)
    val page: State<Int> get() = _page

    private val _bunch = mutableIntStateOf(0)
    val bunch: State<Int> get() = _bunch

    private val _recentHashtags = mutableStateOf<List<NuboRecentHashtag>>(emptyList())
    val recentHashtags: State<List<NuboRecentHashtag>> get() = _recentHashtags

    private val _uiEvent = MutableSharedFlow<ExplorerUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var refreshAfterCurrentLoad = false

    init {
        viewModelScope.launch {
            boardStateSync.mutations.collect(::applyMutation)
        }
    }

    // 게시글 목록 가져오기
    private fun loadPosts() {
        if (_isLoadingMore.value) return

        viewModelScope.launch {
            val requestRevision = boardStateSync.revision
            val requestPage = _page.intValue
            val requestOption = _option.intValue
            val requestKeyword = _keyword.value
            // 처음 로딩할 때는 Loading 상태로 두기
            if (requestPage == 1) {
                _posts.value = NuboResponse.Loading
            }
            _isLoadingMore.value = true

            try {
                val token = getUserInfoUseCase().first().token
                getPostListUseCase(
                    page = requestPage,
                    option = requestOption,
                    keyword = requestKeyword,
                    token = token
                ).collect { response ->
                    when (response) {
                        NuboResponse.Loading -> Unit
                        is NuboResponse.Error -> {
                            AppDiagnostics.report("탐색 사진 목록", response)
                            _posts.value = response
                        }
                        is NuboResponse.Success -> {
                            val posts = boardStateSync.applyToPosts(response.data, requestRevision)
                            if (posts.isEmpty() && requestKeyword.isEmpty()) {
                                _uiEvent.emit(ExplorerUiEvent.UnableToFindPosts)
                            }

                            if (requestPage == 1) {
                                _posts.value = NuboResponse.Success(posts)
                                _bunch.intValue = posts.size
                            } else {
                                val currentPosts =
                                    (_posts.value as? NuboResponse.Success)?.data.orEmpty()
                                _posts.value = NuboResponse.Success(
                                    (currentPosts + posts).distinctBy(NuboPost::uid)
                                )
                            }
                            if (response.data.isNotEmpty()) _page.intValue = requestPage + 1
                        }
                    }
                }
            } finally {
                _isLoadingMore.value = false
                if (refreshAfterCurrentLoad) {
                    refreshAfterCurrentLoad = false
                    _page.intValue = 1
                    loadPosts()
                }
            }
        }
    }

    // 최근 해시태그 목록 가져오기
    fun loadRecentHashtags(limit: Int = 10) {
        viewModelScope.launch {
            getRecentHashtagListUseCase(boardUid = Env.BOARD_UID, limit = limit).collect {
                it.handle { resp ->
                    if (resp.success) {
                        _recentHashtags.value = resp.result
                    } else {
                        _uiEvent.emit(ExplorerUiEvent.UnableToFindRecentHashtags(resp.error))
                    }
                }
            }
        }
    }

    // 게시글 목록 업데이트
    fun refresh(resetPaging: Boolean = false) {
        if (resetPaging) {
            _page.intValue = 1
            if (_isLoadingMore.value) {
                refreshAfterCurrentLoad = true
                return
            }
        }
        loadPosts()
    }

    // 검색 중이 아닐 때 탐색 화면에 들어올 때마다 첫 페이지를 다시 받는다.
    fun refreshOnEnter() {
        if (_keyword.value.isBlank()) refresh(resetPaging = true)
    }

    // 게시글 검색 옵션 업데이트
    fun search(option: Int, keyword: String) {
        _option.intValue = option
        _keyword.value = keyword

        refresh(resetPaging = true)
    }

    // 검색 옵션 업데이트
    fun setOption(option: Int) {
        _option.intValue = option
    }

    // 검색어 업데이트
    fun setKeyword(keyword: String) {
        _keyword.value = keyword
    }

    private fun applyMutation(mutation: BoardMutation) {
        val current = (_posts.value as? NuboResponse.Success)?.data ?: return
        _posts.value = NuboResponse.Success(current.applyPostMutation(mutation))
    }
}

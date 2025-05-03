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
import me.domain.model.board.TsboardPost
import me.domain.repository.TsboardResponse
import me.domain.repository.handle
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.board.GetPostListUseCase
import me.sensta.viewmodel.uievent.ExplorerUiEvent
import javax.inject.Inject

@HiltViewModel
class ExplorerViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getPostListUseCase: GetPostListUseCase
) : ViewModel() {
    private var _posts =
        mutableStateOf<TsboardResponse<List<TsboardPost>>>(TsboardResponse.Loading)
    val posts: State<TsboardResponse<List<TsboardPost>>> get() = _posts

    private val _isLoadingMore = mutableStateOf(false)
    private val _option = mutableIntStateOf(0)
    private val _keyword = mutableStateOf("")
    val keyword: State<String> get() = _keyword

    private val _lastPostUid = mutableIntStateOf(0)

    private val _page = mutableIntStateOf(1)
    val page: State<Int> get() = _page

    private val _bunch = mutableIntStateOf(0)
    val bunch: State<Int> get() = _bunch

    private val _uiEvent = MutableSharedFlow<ExplorerUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadPosts()
    }

    // 게시글 목록 가져오기
    private fun loadPosts() {
        if (_isLoadingMore.value) return

        viewModelScope.launch {
            // 처음 로딩할 때는 Loading 상태로 두기
            if (_lastPostUid.intValue == 0) {
                _posts.value = TsboardResponse.Loading
                _page.intValue = 1
            }
            _isLoadingMore.value = true

            val token = getUserInfoUseCase().first().token
            getPostListUseCase(
                sinceUid = _lastPostUid.intValue,
                option = _option.intValue,
                keyword = _keyword.value,
                token = token
            ).collect {
                it.handle { resp ->
                    if (resp.isEmpty()) {
                        _uiEvent.emit(ExplorerUiEvent.UnableToFindPosts)
                        return@handle
                    }

                    if (_lastPostUid.intValue == 0) {
                        _posts.value = it
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
                        _page.intValue++
                    }
                    _lastPostUid.intValue = resp.last().uid
                }
            }
            _isLoadingMore.value = false
        }
    }

    // 게시글 목록 업데이트
    fun refresh(resetLastUid: Boolean = false) {
        if (resetLastUid) {
            _lastPostUid.intValue = 0
        }
        loadPosts()
    }

    // 게시글 검색 옵션 업데이트
    fun search(option: Int, keyword: String) {
        _option.intValue = option
        _keyword.value = keyword

        refresh(resetLastUid = true)
    }

    // 검색어 업데이트
    fun setKeyword(keyword: String) {
        _keyword.value = keyword
    }
}
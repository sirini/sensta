package me.sensta.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.domain.model.board.TsboardPost
import me.domain.repository.TsboardResponse
import me.domain.usecase.GetPostListUseCase
import me.domain.usecase.auth.GetUserInfoUseCase
import javax.inject.Inject

@HiltViewModel
class ExplorerViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getPostListUseCase: GetPostListUseCase
) : ViewModel() {
    private var _posts =
        mutableStateOf<TsboardResponse<List<TsboardPost>>>(TsboardResponse.Loading)
    val posts: State<TsboardResponse<List<TsboardPost>>> get() = _posts

    private var _isLoadingMore by mutableStateOf(false)
    private var _option by mutableIntStateOf(0)
    private var _keyword by mutableStateOf("")
    private var _lastPostUid by mutableIntStateOf(0)

    private var _page by mutableIntStateOf(1)
    val page: Int get() = _page

    private var _bunch by mutableIntStateOf(0)
    val bunch: Int get() = _bunch

    init {
        loadPosts()
    }

    // 게시글 목록 가져오기
    private fun loadPosts() {
        if (_isLoadingMore) return

        viewModelScope.launch {
            // 처음 로딩할 때는 Loading 상태로 두기
            if (_lastPostUid == 0) {
                _posts.value = TsboardResponse.Loading
                _page = 1
            }
            _isLoadingMore = true

            var token = ""
            getUserInfoUseCase().collect {
                token = it.token
            }

            getPostListUseCase(
                sinceUid = _lastPostUid,
                option = _option,
                keyword = _keyword,
                token = token
            ).collect {
                val postData = (it as TsboardResponse.Success<List<TsboardPost>>).data
                if (_lastPostUid == 0) {
                    _posts.value = it
                    _bunch = it.data.size

                } else {
                    // 이전 게시글들을 이어서 붙여나가기
                    val currentPosts =
                        (_posts.value as TsboardResponse.Success<List<TsboardPost>>).data
                    postData.ifEmpty {
                        _posts.value = TsboardResponse.Success(currentPosts)
                        return@collect
                    }
                    _posts.value = TsboardResponse.Success(currentPosts + postData)
                    _page++
                }
                _lastPostUid = postData.last().uid
            }
            _isLoadingMore = false
        }
    }

    // 게시글 목록 업데이트
    fun refresh(resetLastUid: Boolean = false) {
        if (resetLastUid) {
            _lastPostUid = 0
        }
        loadPosts()
    }

    // 게시글 검색 옵션 업데이트
    fun search(option: Int, keyword: String) {
        _option = option
        _keyword = keyword

        refresh(resetLastUid = true)
    }
}
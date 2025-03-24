package me.sensta.viewmodel

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.domain.model.board.TsboardPost
import me.domain.repository.TsboardResponse
import me.domain.usecase.GetPostListUseCase
import javax.inject.Inject

@HiltViewModel
class ExplorerViewModel @Inject constructor(
    private val getPostListUseCase: GetPostListUseCase
) : ViewModel() {
    private val _posts =
        MutableStateFlow<TsboardResponse<List<TsboardPost>>>(TsboardResponse.Loading)
    val posts = _posts.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore = _isLoadingMore.asStateFlow()

    private val _option = MutableStateFlow(0)
    val option = _option.asStateFlow()

    private val _keyword = MutableStateFlow("")
    val keyword = _keyword.asStateFlow()

    private val _page = MutableStateFlow(1)
    val page = _page.asStateFlow()

    private val _lastPostUid = mutableIntStateOf(0)

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
                _page.value = 1
            }
            _isLoadingMore.value = true

            getPostListUseCase(_lastPostUid.intValue, _option.value, _keyword.value).collect {
                val postData = (it as TsboardResponse.Success<List<TsboardPost>>).data
                if (_lastPostUid.intValue == 0) {
                    _posts.value = it

                } else {
                    // 이전 게시글들을 이어서 붙여나가기
                    val currentPosts =
                        (_posts.value as TsboardResponse.Success<List<TsboardPost>>).data
                    postData.ifEmpty {
                        _posts.value = TsboardResponse.Success(currentPosts)
                        return@collect
                    }
                    _posts.value = TsboardResponse.Success(currentPosts + postData)
                    _page.value++
                }
                _lastPostUid.intValue = postData.last().uid
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
        _option.value = option
        _keyword.value = keyword

        refresh(resetLastUid = true)
    }
}
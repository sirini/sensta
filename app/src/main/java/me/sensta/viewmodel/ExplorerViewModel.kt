package me.sensta.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.domain.model.board.TsboardPost
import me.domain.repository.TsboardResponse
import me.domain.repository.handle
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

    private val _isLoadingMore = mutableStateOf(false)
    private val _option = mutableIntStateOf(0)
    private val _keyword = mutableStateOf("")
    private val _lastPostUid = mutableIntStateOf(0)

    private val _page = mutableIntStateOf(1)
    val page: State<Int> get() = _page

    private val _bunch = mutableIntStateOf(0)
    val bunch: State<Int> get() = _bunch

    init {
        loadPosts()
    }

    // 게시글 목록 가져오기
    private fun loadPosts(context: Context? = null) {
        if (_isLoadingMore.value) return

        viewModelScope.launch {
            // 처음 로딩할 때는 Loading 상태로 두기
            if (_lastPostUid.intValue == 0) {
                _posts.value = TsboardResponse.Loading
                _page.intValue = 1
            }
            _isLoadingMore.value = true

            var token = ""
            getUserInfoUseCase().collect {
                token = it.token
            }

            getPostListUseCase(
                sinceUid = _lastPostUid.intValue,
                option = _option.intValue,
                keyword = _keyword.value,
                token = token
            ).collect {
                it.handle(context) { resp ->
                    if (resp.isEmpty()) {
                        context?.let {
                            Toast.makeText(context, "게시글을 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
                        }
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
    fun refresh(resetLastUid: Boolean = false, context: Context? = null) {
        if (resetLastUid) {
            _lastPostUid.intValue = 0
        }
        loadPosts(context)
    }

    // 게시글 검색 옵션 업데이트
    fun search(option: Int, keyword: String, context: Context? = null) {
        _option.intValue = option
        _keyword.value = keyword

        refresh(resetLastUid = true, context = context)
    }
}
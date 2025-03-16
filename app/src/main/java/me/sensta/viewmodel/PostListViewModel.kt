package me.sensta.viewmodel

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
class PostListViewModel @Inject constructor(
    private val getPostListUseCase: GetPostListUseCase
) : ViewModel() {
    private val _posts =
        MutableStateFlow<TsboardResponse<List<TsboardPost>>>(TsboardResponse.Loading)
    val posts = _posts.asStateFlow()

    init {
        loadPosts()
    }

    // 게시글 목록 가져오기
    private fun loadPosts() {
        viewModelScope.launch {
            getPostListUseCase().collect {
                _posts.value = it
            }
        }
    }

    // 게시글 목록 업데이트
    fun refresh() {
        loadPosts()
    }
}
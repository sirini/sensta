package me.sensta.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.data.env.Env
import me.domain.model.common.TsboardResponseNothing
import me.domain.model.photo.TsboardPhoto
import me.domain.repository.TsboardResponse
import me.domain.usecase.GetPhotoListUseCase
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.board.UpdateLikePostUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPhotoListUseCase: GetPhotoListUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val updateLikePostUseCase: UpdateLikePostUseCase
) : ViewModel() {
    private val _photos =
        mutableStateOf<TsboardResponse<List<TsboardPhoto>>>(TsboardResponse.Loading)
    val photos: State<TsboardResponse<List<TsboardPhoto>>> get() = _photos

    private val _isLoadingMore = mutableStateOf(false)
    val isLoadingMore: State<Boolean> get() = _isLoadingMore

    private val _page = mutableIntStateOf(1)
    val page: State<Int> get() = _page

    private val _bunch = mutableIntStateOf(0)
    val bunch: State<Int> get() = _bunch

    private var _lastPostUid by mutableIntStateOf(0)

    init {
        loadPhotos()
    }

    // 갤러리 사진 목록 가져오기
    private fun loadPhotos() {
        if (_isLoadingMore.value) return

        viewModelScope.launch {
            if (_lastPostUid == 0) {
                _photos.value = TsboardResponse.Loading
                _page.intValue = 1
            }
            _isLoadingMore.value = true

            var token = ""
            getUserInfoUseCase().collect {
                token = it.token
            }

            getPhotoListUseCase(sinceUid = _lastPostUid, token = token).collect {
                val photoData = (it as TsboardResponse.Success<List<TsboardPhoto>>).data
                if (_lastPostUid == 0) {
                    _photos.value = it
                    _bunch.intValue = it.data.size

                } else {
                    // 이전 게시글들을 이어서 붙여나가기
                    val currentPhotos =
                        (_photos.value as TsboardResponse.Success<List<TsboardPhoto>>).data
                    photoData.ifEmpty {
                        _photos.value = TsboardResponse.Success(currentPhotos)
                        return@collect
                    }
                    _photos.value = TsboardResponse.Success(currentPhotos + photoData)
                    _page.intValue++
                }
                _lastPostUid = photoData.last().uid
            }
            _isLoadingMore.value = false
        }
    }

    // 갤러리 목록 업데이트
    fun refresh(resetLastUid: Boolean = false) {
        if (resetLastUid) {
            _lastPostUid = 0
        }
        loadPhotos()
    }

    // 게시글에 좋아요 누르기
    fun like(postUid: Int, liked: Boolean, context: Context? = null) {
        viewModelScope.launch {
            getUserInfoUseCase().collect {
                if (it.token.isEmpty()) return@collect

                updateLikePostUseCase(
                    boardUid = Env.boardUid,
                    postUid = postUid,
                    liked = liked,
                    token = it.token
                ).collect { result ->
                    val data = (result as TsboardResponse.Success<TsboardResponseNothing>).data
                    if (data.success && null != context) {
                        if (liked) {
                            Toast.makeText(context, "이 게시글에 좋아요를 남겼습니다", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "이 게시글의 좋아요를 취소했습니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}
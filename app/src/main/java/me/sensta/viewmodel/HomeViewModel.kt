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
import me.domain.model.photo.TsboardPhoto
import me.domain.repository.TsboardResponse
import me.domain.repository.handle
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.board.GetPhotoListUseCase
import me.domain.usecase.board.UpdateLikePostUseCase
import me.sensta.viewmodel.uievent.HomeUiEvent
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

    private val _lastPostUid = mutableIntStateOf(0)
    private val _uiEvent = MutableSharedFlow<HomeUiEvent>()
    val uiEvent get() = _uiEvent.asSharedFlow()

    init {
        loadPhotos()
    }

    // 갤러리 사진 목록 가져오기
    private fun loadPhotos() {
        if (_isLoadingMore.value) return

        viewModelScope.launch {
            if (_lastPostUid.intValue == 0) {
                _photos.value = TsboardResponse.Loading
                _page.intValue = 1
            }
            _isLoadingMore.value = true

            val token = getUserInfoUseCase().first().token
            getPhotoListUseCase(sinceUid = _lastPostUid.intValue, token = token).collect {
                it.handle { resp ->
                    if (_lastPostUid.intValue == 0) {
                        _photos.value = TsboardResponse.Success(resp)
                        _bunch.intValue = resp.size

                    } else {
                        // 이전 게시글들을 이어서 붙여나가기
                        val currentPhotos =
                            (_photos.value as TsboardResponse.Success<List<TsboardPhoto>>).data
                        resp.ifEmpty {
                            _photos.value = TsboardResponse.Success(currentPhotos)
                            return@handle
                        }
                        _photos.value = TsboardResponse.Success(currentPhotos + resp)
                        _page.intValue++
                    }
                    _lastPostUid.intValue = resp.last().uid
                }
            }
            _isLoadingMore.value = false
        }
    }

    // 갤러리 목록 업데이트
    fun refresh(resetLastUid: Boolean = false) {
        if (resetLastUid) {
            _lastPostUid.intValue = 0
        }
        loadPhotos()
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
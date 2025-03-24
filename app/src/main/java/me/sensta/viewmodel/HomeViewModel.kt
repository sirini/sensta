package me.sensta.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.domain.model.photo.TsboardPhoto
import me.domain.repository.TsboardResponse
import me.domain.usecase.GetPhotoListUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPhotoListUseCase: GetPhotoListUseCase
) : ViewModel() {
    private var _photos by
    mutableStateOf<TsboardResponse<List<TsboardPhoto>>>(TsboardResponse.Loading)
    val photos: TsboardResponse<List<TsboardPhoto>> get() = _photos

    private var _isLoadingMore by mutableStateOf(false)
    val isLoadingMore: Boolean get() = _isLoadingMore

    private var _page by mutableIntStateOf(1)
    val page: Int get() = _page

    private var _bunch by mutableIntStateOf(0)
    val bunch: Int get() = _bunch

    private var _lastPostUid by mutableIntStateOf(0)

    init {
        loadPhotos()
    }

    // 갤러리 사진 목록 가져오기
    private fun loadPhotos() {
        if (_isLoadingMore) return

        viewModelScope.launch {
            if (_lastPostUid == 0) {
                _photos = TsboardResponse.Loading
                _page = 1
            }
            _isLoadingMore = true

            getPhotoListUseCase(sinceUid = _lastPostUid).collect {
                val photoData = (it as TsboardResponse.Success<List<TsboardPhoto>>).data
                if (_lastPostUid == 0) {
                    _photos = it
                    _bunch = it.data.size

                } else {
                    // 이전 게시글들을 이어서 붙여나가기
                    val currentPhotos =
                        (_photos as TsboardResponse.Success<List<TsboardPhoto>>).data
                    photoData.ifEmpty {
                        _photos = TsboardResponse.Success(currentPhotos)
                        return@collect
                    }
                    _photos = TsboardResponse.Success(currentPhotos + photoData)
                    _page++
                }
                _lastPostUid = photoData.last().uid
            }
            _isLoadingMore = false
        }
    }

    // 갤러리 목록 업데이트
    fun refresh(resetLastUid: Boolean = false) {
        if (resetLastUid) {
            _lastPostUid = 0
        }
        loadPhotos()
    }
}
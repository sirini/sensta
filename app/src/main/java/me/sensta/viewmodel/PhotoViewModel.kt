package me.sensta.viewmodel

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.domain.model.photo.TsboardPhoto
import me.domain.repository.TsboardResponse
import me.domain.usecase.GetPhotoListUseCase
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val getPhotoListUseCase: GetPhotoListUseCase
) : ViewModel() {
    private val _photos =
        MutableStateFlow<TsboardResponse<List<TsboardPhoto>>>(TsboardResponse.Loading)
    val photos = _photos.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore = _isLoadingMore.asStateFlow()

    private val _lastPostUid = mutableIntStateOf(0)

    init {
        loadPhotos()
    }

    // 갤러리 사진 목록 가져오기
    private fun loadPhotos() {
        if (_isLoadingMore.value) return

        viewModelScope.launch {
            _photos.value = TsboardResponse.Loading
            _isLoadingMore.value = true

            getPhotoListUseCase(sinceUid = _lastPostUid.intValue).collect {
                _photos.value = it

                if (it is TsboardResponse.Success) {
                    it.data.ifEmpty {
                        return@collect
                    }

                    _lastPostUid.intValue = it.data.last().uid
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
}
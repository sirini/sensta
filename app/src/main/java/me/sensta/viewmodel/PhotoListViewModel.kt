package me.sensta.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.domain.model.gallery.TsboardPhoto
import me.domain.repository.TsboardResponse
import me.domain.usecase.GetPhotoListUseCase
import javax.inject.Inject

@HiltViewModel
class PhotoListViewModel @Inject constructor(
    private val getPhotoListUseCase: GetPhotoListUseCase
) : ViewModel() {
    private val _photos =
        MutableStateFlow<TsboardResponse<List<TsboardPhoto>>>(TsboardResponse.Loading)
    val photos = _photos.asStateFlow()
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    init {
        loadPhotos()
    }

    // 갤러리 사진 목록 가져오기
    private fun loadPhotos(sinceUid: Int = 0) {
        viewModelScope.launch {
            getPhotoListUseCase(sinceUid = sinceUid).collect {
                _photos.value = it
            }
        }
    }

    // 갤러리 목록 업데이트
    fun refresh(sinceUid: Int = 0) {
        loadPhotos(sinceUid = sinceUid)
    }
}
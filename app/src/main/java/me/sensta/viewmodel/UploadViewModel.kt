package me.sensta.viewmodel

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import me.domain.usecase.auth.GetUserInfoUseCase
import me.sensta.viewmodel.state.UploadState
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
) : ViewModel() {
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _uris = mutableStateOf<List<Uri>>(emptyList())
    val uris: State<List<Uri>> get() = _uris

    private val _uploadState = mutableStateOf<UploadState>(UploadState.SelectImage)
    val uploadState: State<UploadState> get() = _uploadState

    // 이미지 파일들의 Uri를 저장하기
    fun setUris(uris: List<Uri>) {
        _uris.value = uris
    }

    // 이미지 업로드 단계 변경
    fun setUploadState(state: UploadState) {
        _uploadState.value = state
    }
}
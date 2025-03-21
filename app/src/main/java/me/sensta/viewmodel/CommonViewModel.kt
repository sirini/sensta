package me.sensta.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import me.domain.model.photo.TsboardPhoto
import me.domain.model.photo.emptyPhoto
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor() : ViewModel() {
    private val _photo = mutableStateOf<TsboardPhoto>(emptyPhoto)
    val photo: State<TsboardPhoto> = _photo

    // 이미 목록에서 가져왔던 사진 정보들 저장하기
    fun setPhoto(photo: TsboardPhoto) {
        _photo.value = photo
    }
}
package me.sensta.viewmodel

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.data.env.Env
import me.domain.repository.handle
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.board.WritePostUseCase
import me.sensta.viewmodel.state.UploadState
import me.sensta.viewmodel.uievent.UploadUiEvent
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val writePostUseCase: WritePostUseCase
) : ViewModel() {
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _uris = mutableStateOf<List<Uri>>(emptyList())
    val uris: State<List<Uri>> get() = _uris

    private val _uploadState = mutableStateOf<UploadState>(UploadState.SelectImage)
    val uploadState: State<UploadState> get() = _uploadState

    private val _title = mutableStateOf("")
    val title: State<String> get() = _title

    private val _content = mutableStateOf("")
    val content: State<String> get() = _content

    private val _tags = mutableStateOf<List<String>>(emptyList())
    val tags: State<List<String>> get() = _tags

    private val _uploadedPostUid = mutableIntStateOf(0)
    val uploadedPostUid: State<Int> get() = _uploadedPostUid

    private val _uiEvent = MutableSharedFlow<UploadUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // 사진에 대한 설명 입력 받기
    fun setContent(content: String) {
        _content.value = content
    }

    // 사진에 대한 태그 입력 받기
    fun addTag(hashtag: String, context: Context) {
        val tag = hashtag.trim().lowercase()
        val regex = Regex("^[a-z0-9가-힣_.]+\$")
        if (!regex.matches(tag)) {
            viewModelScope.launch { _uiEvent.emit(UploadUiEvent.InvalidHashtag) }
            return
        }

        if (_tags.value.contains(tag)) {
            viewModelScope.launch { _uiEvent.emit(UploadUiEvent.AlreadyAddedHashtag) }
            return
        }

        val newTags = _tags.value.toMutableList()
        newTags.add(tag)

        _tags.value = newTags
    }

    // 입력 받았던 태그를 제거하기
    fun removeTag(hashtag: String) {
        val newTags = _tags.value.toMutableList()
        newTags.remove(hashtag)
        _tags.value = newTags

        viewModelScope.launch { _uiEvent.emit(UploadUiEvent.HashtagRemoved(hashtag)) }
    }

    // 사진 제목 입력 받기
    fun setTitle(title: String) {
        _title.value = title
    }

    // 이미지 파일들의 Uri를 저장하기
    fun setUris(uris: List<Uri>, context: Context) {
        viewModelScope.launch {
            val totalSize = withContext(Dispatchers.IO) {
                uris.sumOf { uri ->
                    context.contentResolver.query(
                        uri, arrayOf(OpenableColumns.SIZE), null, null, null
                    )?.use { cursor ->
                        if (cursor.moveToFirst()) cursor.getLong(0) else 0L
                    } ?: 0L
                }
            }
            // 허용된 파일 크기보다 클 경우 알려주기
            if (totalSize > Env.MAX_UPLOAD_SIZE) {
                _uiEvent.emit(UploadUiEvent.FileSizeExceeded(totalSize, Env.MAX_UPLOAD_SIZE))
            } else {
                _uris.value = uris
            }
        }
    }

    // 이미지 업로드 단계 변경
    fun setUploadState(state: UploadState) {
        _uploadState.value = state
    }

    // 게시글 업로드
    fun upload(context: Context) {
        viewModelScope.launch {
            val token = getUserInfoUseCase().first().token
            if (token.isEmpty()) return@launch

            _isLoading.value = true
            writePostUseCase(
                context = context,
                boardUid = Env.BOARD_UID,
                categoryUid = Env.CATEGORY_UID,
                isNotice = false,
                isSecret = false,
                title = _title.value.trim(),
                content = _content.value.trim(),
                tags = _tags.value,
                attachments = _uris.value,
                token = token
            ).collect {
                it.handle { resp ->
                    if (resp.success) {
                        _uiEvent.emit(UploadUiEvent.PostUploaded)
                        _uploadedPostUid.intValue = resp.result
                    } else {
                        _uiEvent.emit(UploadUiEvent.FailedToUpload(resp.error))
                        _uploadedPostUid.intValue = 0
                    }
                }
            }
            _isLoading.value = false
        }
    }
}
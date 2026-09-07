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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.data.env.Env
import me.domain.model.board.NuboEditorConfig
import me.domain.model.board.NuboTagSuggestion
import me.domain.repository.NuboResponse
import me.domain.repository.handle
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.board.WritePostUseCase
import me.domain.usecase.board.GetEditorConfigUseCase
import me.domain.usecase.board.GetTagSuggestionsUseCase
import me.sensta.editor.PhotoEditorState
import me.sensta.editor.PhotoRenderer
import me.sensta.viewmodel.state.UploadState
import me.sensta.viewmodel.uievent.UploadUiEvent
import me.sensta.policy.CommunityPolicyManager
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getEditorConfigUseCase: GetEditorConfigUseCase,
    private val getTagSuggestionsUseCase: GetTagSuggestionsUseCase,
    private val writePostUseCase: WritePostUseCase,
    private val communityPolicyManager: CommunityPolicyManager
) : ViewModel() {
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _uris = mutableStateOf<List<Uri>>(emptyList())
    val uris: State<List<Uri>> get() = _uris

    val photoEditor = PhotoEditorState()

    private val _uploadState = mutableStateOf<UploadState>(UploadState.SelectImage)
    val uploadState: State<UploadState> get() = _uploadState

    private val _title = mutableStateOf("")
    val title: State<String> get() = _title

    private val _content = mutableStateOf("")
    val content: State<String> get() = _content

    private val _tags = mutableStateOf<List<String>>(emptyList())
    val tags: State<List<String>> get() = _tags

    private val _tagSuggestions = mutableStateOf<List<NuboTagSuggestion>>(emptyList())
    val tagSuggestions: State<List<NuboTagSuggestion>> get() = _tagSuggestions
    private var tagSuggestionJob: Job? = null
    private var tagSuggestionQuery = ""

    private val _editorConfig = mutableStateOf<NuboEditorConfig?>(null)
    val editorConfig: State<NuboEditorConfig?> get() = _editorConfig

    private val _selectedCategoryUid = mutableIntStateOf(0)
    val selectedCategoryUid: State<Int> get() = _selectedCategoryUid

    private val _isEditorConfigLoading = mutableStateOf(false)
    val isEditorConfigLoading: State<Boolean> get() = _isEditorConfigLoading

    private val _editorConfigError = mutableStateOf<String?>(null)
    val editorConfigError: State<String?> get() = _editorConfigError

    private val _uploadedPostUid = mutableIntStateOf(0)
    val uploadedPostUid: State<Int> get() = _uploadedPostUid

    private val _isCommunityPolicyAccepted = mutableStateOf(communityPolicyManager.isAccepted())
    val isCommunityPolicyAccepted: State<Boolean> get() = _isCommunityPolicyAccepted

    private val _uiEvent = MutableSharedFlow<UploadUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // 사진에 대한 태그 입력 받기
    fun addTag(hashtag: String) {
        val tag = hashtag.trim().removePrefix("#").lowercase()
        val regex = Regex("^[a-z0-9가-힣_.]+\$")
        if (tag.length !in 2..MAX_TAG_LENGTH || !regex.matches(tag)) {
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
        _tagSuggestions.value = emptyList()
    }

    fun loadEditorConfig() {
        if (_isEditorConfigLoading.value) return
        _isEditorConfigLoading.value = true
        _editorConfigError.value = null
        viewModelScope.launch {
            val token = getUserInfoUseCase().first().token
            when (val response = getEditorConfigUseCase(Env.BOARD_ID, token).first()) {
                is NuboResponse.Success -> {
                    _editorConfig.value = response.data
                    _selectedCategoryUid.intValue = response.data.categories.firstOrNull()?.uid ?: 0
                }
                is NuboResponse.Error -> {
                    _editorConfig.value = null
                    _selectedCategoryUid.intValue = 0
                    _editorConfigError.value = response.message
                }
                NuboResponse.Loading -> Unit
            }
            _isEditorConfigLoading.value = false
        }
    }

    fun selectCategory(uid: Int) {
        if (_editorConfig.value?.categories?.any { it.uid == uid } == true) {
            _selectedCategoryUid.intValue = uid
        }
    }

    fun updateTagSuggestionQuery(input: String) {
        val query = input.trim().removePrefix("#").lowercase()
        tagSuggestionQuery = query
        tagSuggestionJob?.cancel()
        if (query.length < 2) {
            _tagSuggestions.value = emptyList()
            return
        }
        tagSuggestionJob = viewModelScope.launch {
            delay(TAG_SUGGESTION_DEBOUNCE_MILLIS)
            val token = getUserInfoUseCase().first().token
            if (token.isBlank()) return@launch
            when (val response = getTagSuggestionsUseCase(query, TAG_SUGGESTION_LIMIT, token).first()) {
                is NuboResponse.Success -> if (query == tagSuggestionQuery) {
                    _tagSuggestions.value = response.data.filterNot { suggestion ->
                        _tags.value.any { it.equals(suggestion.name, ignoreCase = true) }
                    }
                }
                else -> if (query == tagSuggestionQuery) _tagSuggestions.value = emptyList()
            }
        }
    }

    fun selectTagSuggestion(suggestion: NuboTagSuggestion) {
        addTag(suggestion.name)
        tagSuggestionQuery = ""
        tagSuggestionJob?.cancel()
        _tagSuggestions.value = emptyList()
    }

    // 업로드가 완료되면 uris를 비워주기
    fun clearPreviousUpload(context: Context) {
        photoEditor.clear(context)
        _uploadState.value = UploadState.SelectImage
        _uris.value = emptyList()
        _title.value = ""
        _content.value = ""
        _tags.value = emptyList()
        _uploadedPostUid.intValue = 0
        _tagSuggestions.value = emptyList()
        tagSuggestionQuery = ""
        tagSuggestionJob?.cancel()
    }

    // 입력 받았던 태그를 제거하기
    fun removeTag(hashtag: String) {
        val newTags = _tags.value.toMutableList()
        newTags.remove(hashtag)
        _tags.value = newTags

        viewModelScope.launch { _uiEvent.emit(UploadUiEvent.HashtagRemoved(hashtag)) }
    }

    // 사진에 대한 설명 입력 받기
    fun setContent(content: String) {
        _content.value = content
    }

    // 사진 제목 입력 받기
    fun setTitle(title: String) {
        _title.value = title
    }

    // 이미지 파일들의 Uri를 저장하기
    fun setUris(uris: List<Uri>) {
        _uris.value = uris
        photoEditor.setPhotos(uris)
    }

    fun finishEditing(context: Context) {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val prepared = withContext(Dispatchers.IO) {
                    photoEditor.photos.value.map { photo ->
                        if (photo.renderedUri != null) photo
                        else photo.copy(renderedUri = PhotoRenderer.render(context, photo))
                    }
                }
                val totalSize = prepared.sumOf { photo -> uriSize(context, photo.uploadUri) }
                if (totalSize > Env.MAX_UPLOAD_SIZE) {
                    _uiEvent.emit(UploadUiEvent.FileSizeExceeded(totalSize, Env.MAX_UPLOAD_SIZE))
                } else {
                    photoEditor.setPrepared(prepared)
                    _uploadState.value = UploadState.InputTitle
                }
            } catch (error: Exception) {
                _uiEvent.emit(
                    UploadUiEvent.FailedToEdit(error.localizedMessage ?: "알 수 없는 오류")
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 이미지 업로드 단계 변경
    fun setUploadState(state: UploadState) {
        _uploadState.value = state
    }

    // 기존 회원도 최초 업로드 전에 현재 운영 원칙을 확인하도록 한다.
    fun acceptCommunityPolicy(accepted: Boolean) {
        _isCommunityPolicyAccepted.value = accepted
        if (accepted) communityPolicyManager.accept()
    }

    // 게시글 업로드
    fun beginUploadPreparation() {
        _uploadedPostUid.intValue = 0
        _isLoading.value = true
    }

    fun failUploadPreparation(message: String) {
        _uploadedPostUid.intValue = 0
        _isLoading.value = false
        viewModelScope.launch { _uiEvent.emit(UploadUiEvent.FailedToUpload(message)) }
    }

    fun upload(context: Context) {
        viewModelScope.launch {
            if (!_isCommunityPolicyAccepted.value) {
                _uiEvent.emit(UploadUiEvent.CommunityPolicyRequired)
                _isLoading.value = false
                return@launch
            }
            val token = getUserInfoUseCase().first().token
            if (token.isEmpty()) {
                failUploadPreparation("로그인 상태를 다시 확인해 주세요")
                return@launch
            }
            val config = _editorConfig.value
            val categoryUid = _selectedCategoryUid.intValue
            if (config == null || categoryUid < 1) {
                _uiEvent.emit(UploadUiEvent.FailedToUpload("업로드 설정을 다시 불러와 주세요"))
                _uploadedPostUid.intValue = 0
                _isLoading.value = false
                return@launch
            }

            _isLoading.value = true
            writePostUseCase(
                context = context,
                boardUid = config.boardUid,
                categoryUid = categoryUid,
                isNotice = false,
                isSecret = false,
                title = _title.value.trim(),
                content = _content.value.trim(),
                tags = _tags.value,
                attachments = photoEditor.uploadUris,
                token = token
            ).collect {
                it.handle { resp ->
                    if (resp.success) {
                        _uiEvent.emit(UploadUiEvent.PostUploaded)
                        _uploadedPostUid.intValue = resp.result
                        photoEditor.clearFiles(context)
                    } else {
                        _uiEvent.emit(UploadUiEvent.FailedToUpload(resp.error))
                        _uploadedPostUid.intValue = 0
                    }
                }
            }
            _isLoading.value = false
        }
    }

    private fun uriSize(context: Context, uri: Uri): Long {
        if (uri.scheme == "file") return uri.path?.let { java.io.File(it).length() } ?: 0L
        return runCatching {
            context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.SIZE),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getLong(0) else 0L
            } ?: 0L
        }.getOrDefault(0L)
    }

    companion object {
        private const val TAG_SUGGESTION_LIMIT = 10
        private const val TAG_SUGGESTION_DEBOUNCE_MILLIS = 200L
        private const val MAX_TAG_LENGTH = 30
    }
}

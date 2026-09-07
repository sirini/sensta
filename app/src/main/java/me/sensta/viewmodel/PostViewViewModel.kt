package me.sensta.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.data.env.Env
import me.domain.model.board.NuboBoardViewResponse
import me.domain.model.board.NuboModifyPostParam
import me.domain.model.board.NuboTagSuggestion
import me.domain.repository.NuboResponse
import me.domain.repository.handle
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.board.RemovePostUseCase
import me.domain.usecase.board.ModifyPostUseCase
import me.domain.usecase.board.GetTagSuggestionsUseCase
import me.domain.usecase.view.GetPostViewUseCase
import me.sensta.viewmodel.uievent.ViewUiEvent
import me.sensta.sync.BoardStateSync
import me.sensta.sync.applyMutation
import javax.inject.Inject

@HiltViewModel
class PostViewViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getPostViewUseCase: GetPostViewUseCase,
    private val removePostUseCase: RemovePostUseCase,
    private val modifyPostUseCase: ModifyPostUseCase,
    private val getTagSuggestionsUseCase: GetTagSuggestionsUseCase,
    private val boardStateSync: BoardStateSync
) : ViewModel() {
    private var _post =
        mutableStateOf<NuboResponse<NuboBoardViewResponse>>(NuboResponse.Loading)
    val post: State<NuboResponse<NuboBoardViewResponse>> get() = _post

    private val _openedPosts = mutableListOf<Int>()
    private val _uiEvent = MutableSharedFlow<ViewUiEvent>()
    val uiEvent get() = _uiEvent.asSharedFlow()

    private val _tagSuggestions = mutableStateOf<List<NuboTagSuggestion>>(emptyList())
    val tagSuggestions: State<List<NuboTagSuggestion>> get() = _tagSuggestions
    private var tagSuggestionJob: Job? = null
    private var tagSuggestionQuery = ""

    init {
        viewModelScope.launch {
            boardStateSync.mutations.collect { mutation ->
                val current = (_post.value as? NuboResponse.Success)?.data ?: return@collect
                _post.value = NuboResponse.Success(current.applyMutation(mutation))
            }
        }
    }

    // 게시글 내용 가져오기
    private fun loadPostView(postUid: Int) {
        viewModelScope.launch {
            val requestRevision = boardStateSync.revision
            // 이미 열람한 적 있으면 조회수 올리지 않기
            var needUpdateHit = true
            if (_openedPosts.contains(postUid)) {
                needUpdateHit = false
            } else {
                _openedPosts.add(postUid)
            }

            val token = getUserInfoUseCase().first().token
            getPostViewUseCase(
                postUid = postUid,
                token = token,
                needUpdateHit = needUpdateHit
            ).collect { response ->
                _post.value = when (response) {
                    is NuboResponse.Success -> NuboResponse.Success(
                        boardStateSync.applyToPostView(response.data, requestRevision)
                    )
                    else -> response
                }
            }
        }
    }

    // 게시글 내용 업데이트
    fun refresh(postUid: Int) = loadPostView(postUid = postUid)

    // 게시글 삭제하기
    fun remove(postUid: Int) {
        viewModelScope.launch {
            val token = getUserInfoUseCase().first().token
            if (token.isEmpty()) {
                _uiEvent.emit(ViewUiEvent.FailedToRemovePost("로그인이 필요합니다"))
                return@launch
            }

            removePostUseCase(
                boardUid = Env.BOARD_UID,
                postUid = postUid,
                token = token
            ).collect { response ->
                response.handle(
                    onError = { error ->
                        _uiEvent.emit(ViewUiEvent.FailedToRemovePost(error.message))
                    }
                ) { resp ->
                    if (resp.success) {
                        boardStateSync.removePost(postUid)
                        _uiEvent.emit(ViewUiEvent.PostRemoved)
                    } else {
                        _uiEvent.emit(ViewUiEvent.FailedToRemovePost(resp.error))
                    }
                }
            }
        }
    }

    fun modify(
        postUid: Int,
        categoryUid: Int,
        status: Int,
        title: String,
        content: String,
        tags: List<String>
    ) {
        viewModelScope.launch {
            val token = getUserInfoUseCase().first().token
            if (token.isEmpty()) {
                _uiEvent.emit(ViewUiEvent.FailedToEditPost("로그인이 필요합니다"))
                return@launch
            }

            modifyPostUseCase(
                NuboModifyPostParam(
                    boardUid = Env.BOARD_UID,
                    postUid = postUid,
                    categoryUid = categoryUid,
                    isNotice = status == 1,
                    isSecret = status == 2,
                    title = title.trim(),
                    content = content.trim(),
                    tags = tags,
                    token = token
                )
            ).collect { response ->
                response.handle(
                    onError = { error ->
                        _uiEvent.emit(ViewUiEvent.FailedToEditPost(error.message))
                    }
                ) { result ->
                    if (result.success) {
                        boardStateSync.editPost(postUid, title.trim(), content.trim(), tags)
                        _uiEvent.emit(ViewUiEvent.PostEdited)
                    } else {
                        _uiEvent.emit(ViewUiEvent.FailedToEditPost(result.error))
                    }
                }
            }
        }
    }

    fun updateTagSuggestionQuery(query: String, existingTags: List<String>) {
        val normalized = query.trim().removePrefix("#").lowercase()
        tagSuggestionQuery = normalized
        tagSuggestionJob?.cancel()
        if (normalized.length < 2) {
            _tagSuggestions.value = emptyList()
            return
        }
        tagSuggestionJob = viewModelScope.launch {
            delay(TAG_SUGGESTION_DEBOUNCE_MILLIS)
            val token = getUserInfoUseCase().first().token
            if (token.isBlank()) return@launch
            when (val response = getTagSuggestionsUseCase(normalized, TAG_SUGGESTION_LIMIT, token).first()) {
                is NuboResponse.Success -> if (normalized == tagSuggestionQuery) {
                    _tagSuggestions.value = response.data.filterNot { suggestion ->
                        existingTags.any { it.equals(suggestion.name, ignoreCase = true) }
                    }
                }
                else -> if (normalized == tagSuggestionQuery) _tagSuggestions.value = emptyList()
            }
        }
    }

    fun clearTagSuggestions() {
        tagSuggestionJob?.cancel()
        tagSuggestionQuery = ""
        _tagSuggestions.value = emptyList()
    }

    companion object {
        private const val TAG_SUGGESTION_LIMIT = 10
        private const val TAG_SUGGESTION_DEBOUNCE_MILLIS = 200L
    }
}

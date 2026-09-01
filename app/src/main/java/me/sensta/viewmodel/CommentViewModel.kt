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
import kotlinx.coroutines.launch
import me.data.env.Env
import me.domain.model.board.NuboComment
import me.domain.model.board.NuboModifyCommentParam
import me.domain.model.common.NuboWriter
import me.domain.repository.NuboResponse
import me.domain.repository.handle
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.board.RemoveCommentUseCase
import me.domain.usecase.board.ModifyCommentUseCase
import me.domain.usecase.board.UpdateLikeCommentUseCase
import me.domain.usecase.board.WriteCommentUseCase
import me.domain.usecase.view.GetCommentListUseCase
import me.sensta.viewmodel.uievent.CommentUiEvent
import me.sensta.sync.BoardMutation
import me.sensta.sync.BoardStateSync
import me.sensta.sync.applyCommentMutation
import me.sensta.diagnostics.AppDiagnostics
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getCommentListUseCase: GetCommentListUseCase,
    private val removeCommentUseCase: RemoveCommentUseCase,
    private val modifyCommentUseCase: ModifyCommentUseCase,
    private val updateLikeCommentUseCase: UpdateLikeCommentUseCase,
    private val writeCommentUseCase: WriteCommentUseCase,
    private val boardStateSync: BoardStateSync
) : ViewModel() {
    private val _comments =
        mutableStateOf<NuboResponse<List<NuboComment>>>(NuboResponse.Loading)
    val comments: State<NuboResponse<List<NuboComment>>> get() = _comments

    private val _uiEvent = MutableSharedFlow<CommentUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    private var loadedPostUid: Int? = null
    private var commentLoadJob: Job? = null
    private val pendingLikeComments = mutableSetOf<Int>()

    init {
        viewModelScope.launch {
            boardStateSync.mutations.collect(::applyMutation)
        }
    }

    // 댓글 목록 가져오기
    private fun loadComments(postUid: Int, settlePending: Boolean = false) {
        commentLoadJob?.cancel()
        loadedPostUid = postUid
        if (!settlePending) _comments.value = NuboResponse.Loading
        val requestRevision = boardStateSync.revision
        commentLoadJob = viewModelScope.launch {
            val token = getUserInfoUseCase().first().token
            getCommentListUseCase(postUid = postUid, token = token).collect { response ->
                when (response) {
                    is NuboResponse.Loading -> {
                        if (!settlePending) _comments.value = response
                    }
                    is NuboResponse.Error -> {
                        AppDiagnostics.report("댓글 목록", response)
                        if (!settlePending) _comments.value = response
                    }
                    is NuboResponse.Success -> {
                        if (settlePending) {
                            boardStateSync.reconcileComments(postUid, response.data)
                        }
                        _comments.value = NuboResponse.Success(
                            boardStateSync.applyToComments(postUid, response.data, requestRevision)
                        )
                    }
                }
            }
        }
    }

    // 댓글에 좋아요 클릭하기
    fun like(commentUid: Int, liked: Boolean, currentLikeCount: Int? = null) {
        if (!pendingLikeComments.add(commentUid)) return
        val currentComment = (_comments.value as? NuboResponse.Success)
            ?.data
            ?.firstOrNull { it.uid == commentUid }
        val previousLiked = !liked
        val previousLikeCount = currentLikeCount ?: currentComment?.like
        val expectedLikeCount = previousLikeCount?.let { count ->
            (count + if (liked) 1 else -1).coerceAtLeast(0)
        }
        boardStateSync.changeCommentLike(commentUid, liked, expectedLikeCount)

        viewModelScope.launch {
            try {
                val token = getUserInfoUseCase().first().token
                if (token.isEmpty()) {
                    rollbackLike(commentUid, previousLiked, previousLikeCount, "로그인이 필요합니다")
                    return@launch
                }

                updateLikeCommentUseCase(
                    boardUid = Env.BOARD_UID,
                    commentUid = commentUid,
                    liked = liked,
                    token = token
                ).collect { result ->
                    result.handle(
                        onError = { error ->
                            AppDiagnostics.report("댓글 좋아요", error)
                            rollbackLike(commentUid, previousLiked, previousLikeCount, error.message)
                        }
                    ) { resp ->
                        if (resp.success) {
                            if (liked) {
                                _uiEvent.emit(CommentUiEvent.LikeComment)
                            } else {
                                _uiEvent.emit(CommentUiEvent.CancelLikeComment)
                            }
                        } else {
                            rollbackLike(commentUid, previousLiked, previousLikeCount, resp.error)
                        }
                    }
                }
            } finally {
                pendingLikeComments.remove(commentUid)
            }
        }
    }

    // 댓글 목록 업데이트
    fun refresh(postUid: Int) {
        loadComments(postUid = postUid)
    }

    // 내가 작성한 댓글 삭제하기
    fun remove(removeTargetUid: Int, postUid: Int) {
        viewModelScope.launch {
            val token = getUserInfoUseCase().first().token
            if (token.isEmpty()) {
                _uiEvent.emit(CommentUiEvent.FailedToRemoveComment("로그인이 필요합니다"))
                return@launch
            }

            removeCommentUseCase(
                boardUid = Env.BOARD_UID,
                removeTargetUid = removeTargetUid,
                token = token
            ).collect { result ->
                result.handle(
                    onError = { error ->
                        _uiEvent.emit(CommentUiEvent.FailedToRemoveComment(error.message))
                    }
                ) { resp ->
                    if (resp.success) {
                        val comments = (_comments.value as? NuboResponse.Success)?.data.orEmpty()
                        val hasReplies = comments.any {
                            it.uid != removeTargetUid && it.replyUid == removeTargetUid
                        }
                        boardStateSync.removeComment(removeTargetUid, postUid, hasReplies)
                        _uiEvent.emit(CommentUiEvent.CommentRemoved)
                        loadComments(postUid, settlePending = true)
                    } else {
                        _uiEvent.emit(CommentUiEvent.FailedToRemoveComment(resp.error))
                    }
                }
            }
        }
    }

    // 댓글 작성하기
    fun write(postUid: Int, content: String) {
        viewModelScope.launch {
            val trimmedContent = content.trim()
            if (trimmedContent.length < 10) {
                _uiEvent.emit(CommentUiEvent.FailedToWriteComment("댓글은 10자 이상 입력해 주세요"))
                return@launch
            }
            val user = getUserInfoUseCase().first()
            val token = user.token
            if (token.isEmpty()) {
                _uiEvent.emit(CommentUiEvent.FailedToWriteComment("로그인이 필요합니다"))
                return@launch
            }

            writeCommentUseCase(
                boardUid = Env.BOARD_UID,
                postUid = postUid,
                content = trimmedContent,
                token = token
            ).collect { result ->
                result.handle(
                    onError = { error ->
                        _uiEvent.emit(CommentUiEvent.FailedToWriteComment(error.message))
                    }
                ) { resp ->
                    if (resp.success) {
                        boardStateSync.addComment(
                            NuboComment(
                                uid = resp.result,
                                replyUid = resp.result,
                                postUid = postUid,
                                writer = NuboWriter(
                                    uid = user.uid,
                                    name = user.name,
                                    profile = user.profile,
                                    signature = user.signature
                                ),
                                like = 0,
                                liked = false,
                                submitted = LocalDateTime.now(),
                                status = 0,
                                content = trimmedContent
                            )
                        )
                        _uiEvent.emit(CommentUiEvent.WroteComment)
                        loadComments(postUid, settlePending = true)
                    } else {
                        _uiEvent.emit(CommentUiEvent.FailedToWriteComment(resp.error))
                    }
                }
            }
        }
    }

    fun modify(commentUid: Int, postUid: Int, content: String) {
        viewModelScope.launch {
            val trimmedContent = content.trim()
            if (trimmedContent.length < 2) {
                _uiEvent.emit(CommentUiEvent.FailedToEditComment("댓글 내용을 입력해 주세요"))
                return@launch
            }
            val token = getUserInfoUseCase().first().token
            if (token.isEmpty()) {
                _uiEvent.emit(CommentUiEvent.FailedToEditComment("로그인이 필요합니다"))
                return@launch
            }

            modifyCommentUseCase(
                NuboModifyCommentParam(
                    boardUid = Env.BOARD_UID,
                    postUid = postUid,
                    commentUid = commentUid,
                    content = trimmedContent.toSafeCommentHtml(),
                    token = token
                )
            ).collect { response ->
                response.handle(
                    onError = { error ->
                        _uiEvent.emit(CommentUiEvent.FailedToEditComment(error.message))
                    }
                ) { result ->
                    if (result.success) {
                        boardStateSync.editComment(commentUid, trimmedContent)
                        _uiEvent.emit(CommentUiEvent.CommentEdited)
                    } else {
                        _uiEvent.emit(CommentUiEvent.FailedToEditComment(result.error))
                    }
                }
            }
        }
    }

    private fun applyMutation(mutation: BoardMutation) {
        val current = (_comments.value as? NuboResponse.Success)?.data ?: return
        if (mutation is BoardMutation.CommentAdded && mutation.comment.postUid != loadedPostUid) return
        if (mutation is BoardMutation.CommentRemoved && mutation.postUid != loadedPostUid) return
        _comments.value = NuboResponse.Success(current.applyCommentMutation(mutation))
    }

    private suspend fun rollbackLike(
        commentUid: Int,
        liked: Boolean,
        expectedLikeCount: Int?,
        message: String
    ) {
        boardStateSync.changeCommentLike(commentUid, liked, expectedLikeCount)
        _uiEvent.emit(CommentUiEvent.FailedToUpdateLike(message))
    }
}

internal fun String.toSafeCommentHtml(): String = replace("&", "&amp;")
    .replace("<", "&lt;")
    .replace(">", "&gt;")
    .replace("\"", "&quot;")
    .replace("'", "&#39;")

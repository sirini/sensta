package me.sensta.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.domain.model.board.NuboPost
import me.domain.model.common.NuboWriter
import me.domain.model.user.NuboChatHistory
import me.domain.model.user.NuboOtherUserInfoResult
import me.domain.repository.NuboResponse
import me.domain.repository.handle
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.board.GetPostListUseCase
import me.domain.usecase.user.GetChatHistoryUseCase
import me.domain.usecase.user.GetOtherUserInfoUseCase
import me.domain.usecase.user.MarkChatReadUseCase
import me.domain.usecase.user.GetUserSafetyStatusUseCase
import me.domain.usecase.user.ReportUserUseCase
import me.domain.usecase.user.ChangeUserBlockUseCase
import me.domain.usecase.user.SendChatUseCase
import me.sensta.viewmodel.uievent.ChatUiEvent
import me.sensta.push.PushEventBus
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class UserChatViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getPostListUseCase: GetPostListUseCase,
    private val getOtherUserInfoUseCase: GetOtherUserInfoUseCase,
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val markChatReadUseCase: MarkChatReadUseCase,
    private val sendChatUseCase: SendChatUseCase,
    private val getUserSafetyStatusUseCase: GetUserSafetyStatusUseCase,
    private val reportUserUseCase: ReportUserUseCase,
    private val changeUserBlockUseCase: ChangeUserBlockUseCase,
    private val pushEventBus: PushEventBus
) : ViewModel() {
    private val _otherUser =
        mutableStateOf(
            NuboOtherUserInfoResult(
                uid = 0,
                name = "",
                profile = "",
                level = 0,
                signature = "",
                signup = LocalDateTime.now(),
                signin = LocalDateTime.now(),
                admin = false,
                blocked = false,
                badges = emptyList()
            )
        )
    val otherUser: State<NuboOtherUserInfoResult> get() = _otherUser

    private val _userPosts =
        mutableStateOf<NuboResponse<List<NuboPost>>>(NuboResponse.Loading)
    val userPosts: State<NuboResponse<List<NuboPost>>> get() = _userPosts
    private val _userPostPage = mutableIntStateOf(1)
    private val _isLoadingUserPosts = mutableStateOf(false)
    private var userPostTargetUid = 0
    private var userPostWriterName = ""
    private var userPostRequestId = 0
    private var hasMoreUserPosts = true

    private val _chatMessage = mutableStateOf("")
    val chatMessage: State<String> get() = _chatMessage

    private val _chatHistory = MutableStateFlow<List<NuboChatHistory>>(emptyList())
    val chatHistory: MutableStateFlow<List<NuboChatHistory>> get() = _chatHistory
    private var chatHistoryJob: Job? = null
    private var chatMutationRevision = 0
    private var markedIncomingThroughUid = 0
    private var isConversationVisible = false

    private val _isLoadingInfo = mutableStateOf(false)
    val isLoadingInfo: State<Boolean> get() = _isLoadingInfo

    private val _isLoadingChat = mutableStateOf(false)
    val isLoadingChat: State<Boolean> get() = _isLoadingChat

    private val _isReported = mutableStateOf(false)
    val isReported: State<Boolean> get() = _isReported

    private val _isBlockedByMe = mutableStateOf(false)
    val isBlockedByMe: State<Boolean> get() = _isBlockedByMe

    private val _uiEvent = MutableSharedFlow<ChatUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            pushEventBus.events.collect { event ->
                if (event.notificationType == CHAT_NOTIFICATION_TYPE &&
                    event.fromUserUid == _otherUser.value.uid
                ) {
                    loadChatHistory(showLoading = false)
                }
            }
        }
    }

    // 상대방과의 대화 목록 가져오기
    fun loadChatHistory(showLoading: Boolean = true) {
        if (chatHistoryJob?.isActive == true) return
        if (showLoading) _isLoadingChat.value = true

        chatHistoryJob = viewModelScope.launch {
            val targetUserUid = _otherUser.value.uid
            val requestRevision = chatMutationRevision
            val token = getUserInfoUseCase().first().token
            if (token.isEmpty() || targetUserUid < 1) return@launch

            if (_isBlockedByMe.value) {
                _chatHistory.value = emptyList()
                return@launch
            }

            getChatHistoryUseCase(
                targetUserUid = targetUserUid,
                limit = 100,
                token = token
            ).collect { response ->
                response.handle { resp ->
                    if (_otherUser.value.uid == targetUserUid) {
                        // 전송 중 발생한 폴링 응답이 방금 추가한 메시지를 덮어쓰지 않게 한다.
                        if (requestRevision == chatMutationRevision) {
                            _chatHistory.value = resp.result
                        }
                        markLatestIncomingRead(resp.result, targetUserUid, token)
                    }
                }
            }
        }.also { job ->
            job.invokeOnCompletion {
                if (showLoading) _isLoadingChat.value = false
                if (chatHistoryJob === job) chatHistoryJob = null
            }
        }
    }

    fun setConversationVisible(visible: Boolean) {
        isConversationVisible = visible
    }

    private suspend fun markLatestIncomingRead(
        history: List<NuboChatHistory>,
        targetUserUid: Int,
        token: String
    ) {
        if (!isConversationVisible) return
        val throughUid = latestIncomingMessageUid(history, targetUserUid) ?: return
        if (throughUid <= markedIncomingThroughUid) return

        markChatReadUseCase(targetUserUid, throughUid, token).collect { response ->
            response.handle { result ->
                if (_otherUser.value.uid == targetUserUid) {
                    markedIncomingThroughUid = maxOf(markedIncomingThroughUid, result.throughUid)
                }
            }
        }
    }

    // 다른 사용자의 기본 정보 열어보기
    fun loadOtherUserInfo(user: NuboWriter) {
        _isLoadingInfo.value = true
        resetUserSafetyStatus()
        _otherUser.value = _otherUser.value.copy(
            uid = user.uid,
            name = user.name,
            profile = user.profile,
            signature = user.signature
        )
        loadUserPosts(user.uid, user.name)

        viewModelScope.launch {
            getOtherUserInfoUseCase(user.uid).collect {
                it.handle { resp ->
                    _otherUser.value = resp
                }
            }
            loadUserSafetyStatus(user.uid)
            _isLoadingInfo.value = false
        }
    }

    // 푸시 알림처럼 사용자 번호만 아는 진입점에서 대화 상대를 선택한다.
    fun loadOtherUserInfo(userUid: Int) {
        if (userUid < 1) return
        _isLoadingInfo.value = true
        resetUserSafetyStatus()
        _otherUser.value = _otherUser.value.copy(uid = userUid)

        viewModelScope.launch {
            getOtherUserInfoUseCase(userUid).collect {
                it.handle { resp ->
                    _otherUser.value = resp
                    loadUserPosts(resp.uid, resp.name)
                }
            }
            loadUserSafetyStatus(userUid)
            _isLoadingInfo.value = false
        }
    }

    // 메시지 작성 시 호출
    fun onMessageChange(message: String) {
        _chatMessage.value = message
    }

    // 메시지 보내기
    fun sendMessage() {
        viewModelScope.launch {
            if (_isBlockedByMe.value) return@launch
            val userInfo = getUserInfoUseCase().first()
            if (userInfo.token.isEmpty()) return@launch
            val outgoingMessage = _chatMessage.value.trim()
            if (outgoingMessage.isEmpty() ||
                outgoingMessage.codePointCount(0, outgoingMessage.length) > MAX_CHAT_MESSAGE_LENGTH
            ) return@launch

            sendChatUseCase(
                targetUserUid = _otherUser.value.uid,
                message = outgoingMessage,
                token = userInfo.token
            ).collect {
                it.handle { resp ->
                    if (resp.success && resp.result > 0) {
                        val updated = _chatHistory.value.toMutableList()
                        updated.add(
                            NuboChatHistory(
                                uid = resp.result,
                                userUid = userInfo.uid,
                                message = outgoingMessage,
                                timestamp = LocalDateTime.now(),
                                readAt = 0
                            )
                        )
                        chatMutationRevision++
                        _chatHistory.value = updated
                        _chatMessage.value = ""
                    } else {
                        _uiEvent.emit(ChatUiEvent.FailedToSendChat)
                    }
                }
            }
        }
    }

    // 로그인한 사용자의 신고 및 차단 상태를 서버와 동기화한다.
    private suspend fun loadUserSafetyStatus(targetUserUid: Int) {
        val currentUser = getUserInfoUseCase().first()
        if (currentUser.token.isBlank() || currentUser.uid == targetUserUid) {
            _isReported.value = false
            _isBlockedByMe.value = false
            return
        }
        getUserSafetyStatusUseCase(targetUserUid, currentUser.token).collect {
            it.handle { status ->
                _isReported.value = status.isReported
                _isBlockedByMe.value = status.isBlockedByMe
                if (status.isBlockedByMe) _chatHistory.value = emptyList()
            }
        }
    }

    private fun resetUserSafetyStatus() {
        chatHistoryJob?.cancel()
        chatHistoryJob = null
        chatMutationRevision++
        markedIncomingThroughUid = 0
        _isReported.value = false
        _isBlockedByMe.value = false
        _chatHistory.value = emptyList()
        _userPosts.value = NuboResponse.Loading
        _isLoadingUserPosts.value = false
        userPostTargetUid = 0
        userPostWriterName = ""
        userPostRequestId++
    }

    // 작성자 검색 결과를 UID로 다시 확인해 동명이인의 사진이 섞이지 않게 한다.
    private fun loadUserPosts(
        targetUserUid: Int,
        writerName: String,
        resetPaging: Boolean = true
    ) {
        if (targetUserUid < 1 || writerName.isBlank()) return
        if (resetPaging) {
            userPostRequestId++
            userPostTargetUid = targetUserUid
            userPostWriterName = writerName
            _userPostPage.intValue = 1
            _userPosts.value = NuboResponse.Loading
            hasMoreUserPosts = true
        } else if (_isLoadingUserPosts.value || !hasMoreUserPosts) {
            return
        }
        val requestId = userPostRequestId
        val page = _userPostPage.intValue
        _isLoadingUserPosts.value = true

        viewModelScope.launch {
            val token = getUserInfoUseCase().first().token
            getPostListUseCase(
                page = page,
                option = WRITER_SEARCH_OPTION,
                keyword = writerName,
                token = token
            ).collect { response ->
                if (requestId != userPostRequestId) return@collect
                _userPosts.value = when (response) {
                    is NuboResponse.Success -> {
                        val filtered = response.data.filter { it.writer.uid == targetUserUid }
                        val current = (_userPosts.value as? NuboResponse.Success)?.data.orEmpty()
                        if (response.data.isNotEmpty()) {
                            _userPostPage.intValue++
                        } else {
                            hasMoreUserPosts = false
                        }
                        NuboResponse.Success(
                            if (page == 1) filtered else current + filtered
                        )
                    }
                    is NuboResponse.Error -> {
                        if (page == 1) response else _userPosts.value
                    }
                    is NuboResponse.Loading -> NuboResponse.Loading
                }
            }
            if (requestId == userPostRequestId) _isLoadingUserPosts.value = false
        }
    }

    fun loadMoreUserPosts() {
        loadUserPosts(
            targetUserUid = userPostTargetUid,
            writerName = userPostWriterName,
            resetPaging = false
        )
    }

    // 사용자나 사용자가 작성한 특정 사진을 운영진에게 신고한다.
    fun reportUser(targetUserUid: Int, content: String) {
        viewModelScope.launch {
            val currentUser = getUserInfoUseCase().first()
            if (currentUser.token.isBlank() || currentUser.uid == targetUserUid) return@launch
            reportUserUseCase(targetUserUid, content.trim(), currentUser.token).collect { response ->
                response.handle { result ->
                    if (result.success) {
                        _isReported.value = true
                        _uiEvent.emit(ChatUiEvent.UserReported)
                    } else {
                        _uiEvent.emit(ChatUiEvent.FailedToReport(result.error))
                    }
                }
                if (response is me.domain.repository.NuboResponse.Error) {
                    _uiEvent.emit(ChatUiEvent.FailedToReport(response.message))
                }
            }
        }
    }

    // 차단하면 기존 대화를 즉시 화면에서 숨기고 메시지 전송도 막는다.
    fun changeBlockStatus() {
        viewModelScope.launch {
            val currentUser = getUserInfoUseCase().first()
            val targetUserUid = _otherUser.value.uid
            if (currentUser.token.isBlank() || currentUser.uid == targetUserUid) return@launch
            val shouldBlock = !_isBlockedByMe.value
            changeUserBlockUseCase(targetUserUid, shouldBlock, currentUser.token).collect { response ->
                response.handle { result ->
                    if (result.success) {
                        _isBlockedByMe.value = shouldBlock
                        if (shouldBlock) {
                            _chatHistory.value = emptyList()
                            _uiEvent.emit(ChatUiEvent.UserBlocked)
                        } else {
                            _uiEvent.emit(ChatUiEvent.UserUnblocked)
                            loadChatHistory()
                        }
                    } else {
                        _uiEvent.emit(ChatUiEvent.FailedToChangeBlock(result.error))
                    }
                }
                if (response is me.domain.repository.NuboResponse.Error) {
                    _uiEvent.emit(ChatUiEvent.FailedToChangeBlock(response.message))
                }
            }
        }
    }

    companion object {
        const val MAX_CHAT_MESSAGE_LENGTH = 2_000
        private const val CHAT_NOTIFICATION_TYPE = 4
        private const val WRITER_SEARCH_OPTION = 2
    }
}

internal fun latestIncomingMessageUid(
    history: List<NuboChatHistory>,
    targetUserUid: Int
): Int? = history.lastOrNull { it.userUid == targetUserUid }?.uid

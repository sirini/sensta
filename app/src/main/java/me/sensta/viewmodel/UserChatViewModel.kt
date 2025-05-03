package me.sensta.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.domain.model.common.TsboardWriter
import me.domain.model.user.TsboardChatHistory
import me.domain.model.user.TsboardOtherUserInfoResult
import me.domain.repository.handle
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.user.GetChatHistoryUseCase
import me.domain.usecase.user.GetOtherUserInfoUseCase
import me.domain.usecase.user.SendChatUseCase
import me.sensta.viewmodel.uievent.ChatUiEvent
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class UserChatViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getOtherUserInfoUseCase: GetOtherUserInfoUseCase,
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val sendChatUseCase: SendChatUseCase
) : ViewModel() {
    private val _otherUser =
        mutableStateOf(
            TsboardOtherUserInfoResult(
                uid = 0,
                name = "",
                profile = "",
                level = 0,
                signature = "",
                signup = LocalDateTime.now(),
                signin = LocalDateTime.now(),
                admin = false,
                blocked = false
            )
        )
    val otherUser: State<TsboardOtherUserInfoResult> get() = _otherUser

    private val _chatMessage = mutableStateOf("")
    val chatMessage: State<String> get() = _chatMessage

    private val _chatHistory = MutableStateFlow<List<TsboardChatHistory>>(emptyList())
    val chatHistory: MutableStateFlow<List<TsboardChatHistory>> get() = _chatHistory

    private val _isLoadingInfo = mutableStateOf(false)
    val isLoadingInfo: State<Boolean> get() = _isLoadingInfo

    private val _isLoadingChat = mutableStateOf(false)
    val isLoadingChat: State<Boolean> get() = _isLoadingChat

    private val _uiEvent = MutableSharedFlow<ChatUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // 상대방과의 대화 목록 가져오기
    fun loadChatHistory() {
        _isLoadingChat.value = true

        viewModelScope.launch {
            val token = getUserInfoUseCase().first().token
            if (token.isEmpty()) return@launch

            getChatHistoryUseCase(
                targetUserUid = _otherUser.value.uid,
                limit = 100,
                token = token
            ).collect {
                it.handle { resp ->
                    _chatHistory.value = resp.result.reversed()
                }
            }
            _isLoadingChat.value = false
        }
    }

    // 다른 사용자의 기본 정보 열어보기
    fun loadOtherUserInfo(user: TsboardWriter) {
        _isLoadingInfo.value = true
        _otherUser.value = _otherUser.value.copy(
            uid = user.uid,
            name = user.name,
            profile = user.profile,
            signature = user.signature
        )

        viewModelScope.launch {
            getOtherUserInfoUseCase(user.uid).collect {
                it.handle { resp ->
                    _otherUser.value = resp
                }
            }
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
            val userInfo = getUserInfoUseCase().first()
            if (userInfo.token.isEmpty()) return@launch

            sendChatUseCase(
                targetUserUid = _otherUser.value.uid,
                message = _chatMessage.value,
                token = userInfo.token
            ).collect {
                it.handle { resp ->
                    if (resp.success) {
                        val updated = _chatHistory.value.toMutableList()
                        updated.add(
                            TsboardChatHistory(
                                uid = 0,
                                userUid = userInfo.uid,
                                message = _chatMessage.value,
                                timestamp = LocalDateTime.now()
                            )
                        )
                        _chatHistory.value = updated
                    } else {
                        _uiEvent.emit(ChatUiEvent.FailedToSendChat)
                    }
                }
            }
            _chatMessage.value = ""
        }
    }
}
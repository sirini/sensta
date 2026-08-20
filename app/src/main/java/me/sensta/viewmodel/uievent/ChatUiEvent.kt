package me.sensta.viewmodel.uievent

// UserViewModel UI용 이벤트 인터페이스
sealed interface ChatUiEvent {
    data object FailedToSendChat : ChatUiEvent
    data object UserReported : ChatUiEvent
    data class FailedToReport(val message: String) : ChatUiEvent
    data object UserBlocked : ChatUiEvent
    data object UserUnblocked : ChatUiEvent
    data class FailedToChangeBlock(val message: String) : ChatUiEvent
}

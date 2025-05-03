package me.sensta.viewmodel.uievent

// UserViewModel UI용 이벤트 인터페이스
sealed interface ChatUiEvent {
    data object FailedToSendChat : ChatUiEvent
}
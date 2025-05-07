package me.sensta.viewmodel.uievent

// AuthViewModel UI 이벤트용 인터페이스(로그인)
sealed interface LoginUiEvent {
    data class FailedToLogin(val message: String) : LoginUiEvent
    data class FailedToLoginByGoogle(val message: String) : LoginUiEvent
    data class IDNotFound(val message: String) : LoginUiEvent
}
package me.sensta.viewmodel.uievent

// AuthViewModel UI 이벤트용 인터페이스(프로필용)
sealed interface ProfileUiEvent {
    data object ChangedName : ProfileUiEvent
    data object ChangedSignature : ProfileUiEvent
    data class FailedToChangeName(val message: String) : ProfileUiEvent
    data class FailedToChangeSignature(val message: String) : ProfileUiEvent
    data class FailedToUpdateProfileImage(val message: String) : ProfileUiEvent
    data object ProfileImageUpdated : ProfileUiEvent
}
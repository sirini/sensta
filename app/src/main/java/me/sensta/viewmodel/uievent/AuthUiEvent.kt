package me.sensta.viewmodel.uievent

// AuthViewModel UI 이벤트용 인터페이스
sealed interface AuthUiEvent {
    data object AccessTokenUpdated : AuthUiEvent
    data object AlreadyUsedID : AuthUiEvent
    data object AlreadyUsedName : AuthUiEvent
    data object AtLeast8Characters : AuthUiEvent
    data object ChangedName : AuthUiEvent
    data object ChangedSignature : AuthUiEvent
    data object DifferentPassword : AuthUiEvent
    data object EnterAllInfo : AuthUiEvent
    data object EnterVerificationCode : AuthUiEvent
    data object ExpiredAccessToken : AuthUiEvent
    data object FailedToChangeSignature : AuthUiEvent
    data object FailedToChangeName : AuthUiEvent
    data object FailedToLogin : AuthUiEvent
    data object FailedToLoginByGoogle : AuthUiEvent
    data object FailedToSignUp : AuthUiEvent
    data object FailedToUpdateProfileImage : AuthUiEvent
    data object InvalidEmailAddress : AuthUiEvent
    data object IDNotFound : AuthUiEvent
    data object InvalidName : AuthUiEvent
    data object InvalidTargetUser : AuthUiEvent
    data object ProfileImageUpdated : AuthUiEvent
    data object RemindPasswordRule : AuthUiEvent
    data class SentVerificationCode(val email: String) : AuthUiEvent
    data object SignupCompleted : AuthUiEvent
    data object WrongVerificationCode : AuthUiEvent
}

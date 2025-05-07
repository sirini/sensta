package me.sensta.viewmodel.uievent

// AuthViewModel UI 이벤트용 인터페이스(프로필, 로그인 이외)
sealed interface AuthUiEvent {
    data object AccessTokenUpdated : AuthUiEvent
    data object AlreadyUsedID : AuthUiEvent
    data object AlreadyUsedName : AuthUiEvent
    data object AtLeast8Characters : AuthUiEvent
    data object DifferentPassword : AuthUiEvent
    data object EnterAllInfo : AuthUiEvent
    data object EnterVerificationCode : AuthUiEvent
    data object ExpiredAccessToken : AuthUiEvent
    data object FailedToSignUp : AuthUiEvent
    data object InvalidEmailAddress : AuthUiEvent
    data object InvalidName : AuthUiEvent
    data object InvalidTargetUser : AuthUiEvent
    data object RemindPasswordRule : AuthUiEvent
    data class SentVerificationCode(val email: String) : AuthUiEvent
    data object SignupCompleted : AuthUiEvent
    data object WrongVerificationCode : AuthUiEvent
}

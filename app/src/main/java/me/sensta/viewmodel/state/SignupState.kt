package me.sensta.viewmodel.state

// 회원가입 단계 정의
sealed class SignupState {
    object InputEmail : SignupState()
    object InputPassword : SignupState()
    object InputName : SignupState()
    object InputCode : SignupState()
    object SignupCompleted : SignupState()
}

// 아이디 체크 코드 확인
const val ID_INVALID = 3
const val ID_REGISTERED = 5
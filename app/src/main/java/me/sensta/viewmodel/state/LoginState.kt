package me.sensta.viewmodel.state

// 로그인 단계 정의
sealed class LoginState {
    object InputEmail : LoginState()
    object InputPassword : LoginState()
    object LoginCompleted : LoginState()
}
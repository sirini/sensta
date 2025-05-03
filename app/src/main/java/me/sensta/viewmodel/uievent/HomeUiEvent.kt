package me.sensta.viewmodel.uievent

// HomeViewModel UI 이벤트용 인터페이스
sealed interface HomeUiEvent {
    data object LikePost : HomeUiEvent
    data object CancelLikePost : HomeUiEvent
}
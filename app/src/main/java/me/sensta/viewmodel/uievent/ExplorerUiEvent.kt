package me.sensta.viewmodel.uievent

// ExplorerViewModel UI 이벤트용 인터페이스
sealed interface ExplorerUiEvent {
    data object UnableToFindPosts : ExplorerUiEvent
}
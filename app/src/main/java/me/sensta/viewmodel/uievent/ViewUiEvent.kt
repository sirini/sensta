package me.sensta.viewmodel.uievent

// PostViewViewModel UI용 이벤트 인터페이스
sealed interface ViewUiEvent {
    data object PostRemoved : ViewUiEvent
    data object PostEdited : ViewUiEvent
    data class FailedToRemovePost(val message: String) : ViewUiEvent
    data class FailedToEditPost(val message: String) : ViewUiEvent
}

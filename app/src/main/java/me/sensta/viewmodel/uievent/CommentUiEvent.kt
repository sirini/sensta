package me.sensta.viewmodel.uievent

// CommentViewModel UI 이벤트용 인터페이스
sealed interface CommentUiEvent {
    data object CancelLikeComment : CommentUiEvent
    data object CommentRemoved : CommentUiEvent
    data class FailedToRemoveComment(val message: String) : CommentUiEvent
    data class FailedToWriteComment(val message: String) : CommentUiEvent
    data object LikeComment : CommentUiEvent
    data object WroteComment : CommentUiEvent
}
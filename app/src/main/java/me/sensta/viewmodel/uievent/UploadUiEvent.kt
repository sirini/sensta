package me.sensta.viewmodel.uievent

// UploadViewModel UI 이벤트용 인터페이스
sealed interface UploadUiEvent {
    data class FailedToUpload(val message: String) : UploadUiEvent
    data class FileSizeExceeded(val current: Long, val limit: Long) : UploadUiEvent
    data object InvalidHashtag : UploadUiEvent
    data object AlreadyAddedHashtag : UploadUiEvent
    data class HashtagRemoved(val tag: String) : UploadUiEvent
    data object PostUploaded : UploadUiEvent
}
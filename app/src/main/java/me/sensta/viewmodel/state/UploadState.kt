package me.sensta.viewmodel.state

// 업로드 단계 정의
sealed class UploadState {
    object SelectImage : UploadState()
    object InputTitle : UploadState()
    object InputContent : UploadState()
    object InputTag : UploadState()
    object UploadCompleted : UploadState()
}
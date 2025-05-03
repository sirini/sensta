package me.sensta.viewmodel.uievent

// NotificationViewModel UI 이벤트용 인터페이스
sealed interface NotificationUiEvent {
    data object AllNotificationsChecked : NotificationUiEvent
    data object NotificationListUpdated : NotificationUiEvent
}
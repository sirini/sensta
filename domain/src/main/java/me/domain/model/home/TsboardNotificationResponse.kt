package me.domain.model.home

// 알림 내역 조회에 대한 응답 엔티티
data class TsboardNotificationResponse(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: List<TsboardNotification>
)
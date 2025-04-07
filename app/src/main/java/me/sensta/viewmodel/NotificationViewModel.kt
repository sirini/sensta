package me.sensta.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.domain.model.home.TsboardNotification
import me.domain.repository.TsboardResponse
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.home.GetNotificationUseCase
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationUseCase: GetNotificationUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {
    private var _notifications by mutableStateOf<TsboardResponse<List<TsboardNotification>>>(
        TsboardResponse.Loading
    )
    val notifications: TsboardResponse<List<TsboardNotification>> get() = _notifications

    private var _notificationCount by mutableStateOf(0)
    val notificationCount: Int get() = _notificationCount

    init {
        loadNotifications()
    }

    // 사용자에게 온 알림 목록 가져오기
    fun loadNotifications() {
        viewModelScope.launch {
            getUserInfoUseCase().collect {
                if (it.token.isEmpty()) {
                    _notifications = TsboardResponse.Success(emptyList())
                    _notificationCount = 0
                    return@collect
                }

                getNotificationUseCase(it.token, 10).collect {
                    _notifications = it
                }
            }
        }
    }

    // 확인한 알림 갯수 업데이트 하기
    fun updateNotificationCount(count: Int) {
        _notificationCount = count
    }
}
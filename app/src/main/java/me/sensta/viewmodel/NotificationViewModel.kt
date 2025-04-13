package me.sensta.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.domain.model.home.TsboardNotification
import me.domain.repository.TsboardResponse
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.home.CheckAllNotificationUseCase
import me.domain.usecase.home.CheckNotificationUseCase
import me.domain.usecase.home.GetNotificationUseCase
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationUseCase: GetNotificationUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val checkNotificationUseCase: CheckNotificationUseCase,
    private val checkAllNotificationUseCase: CheckAllNotificationUseCase
) : ViewModel() {
    private val _notifications = mutableStateOf<TsboardResponse<List<TsboardNotification>>>(
        TsboardResponse.Loading
    )
    val notifications: State<TsboardResponse<List<TsboardNotification>>> get() = _notifications

    private val _hasUncheckedNotification = mutableStateOf(false)
    val hasUncheckedNotification: State<Boolean> get() = _hasUncheckedNotification

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    init {
        loadNotifications()
    }

    // 사용자 정보 가져오기 헬퍼 함수
    suspend fun getUserInfo() = getUserInfoUseCase().first()

    // 알림 가져오기 헬퍼 함수
    suspend fun getNotification(token: String, limit: Int) =
        getNotificationUseCase(token, limit).first()

    // 사용자에게 온 알림 목록 가져오기
    fun loadNotifications(context: Context? = null) {
        _isLoading.value = true
        viewModelScope.launch {
            val user = getUserInfo()
            if (user.token.isEmpty()) {
                _notifications.value = TsboardResponse.Success(emptyList())
                _hasUncheckedNotification.value = false
                _isLoading.value = false
                return@launch
            }

            val noti = getNotification(user.token, 20)
            val notiResponse = (noti as TsboardResponse.Success)
            _notifications.value = notiResponse
            _hasUncheckedNotification.value = notiResponse.data.count { !it.checked } > 0
            _isLoading.value = false

            context?.let {
                Toast.makeText(context, "알림 목록을 업데이트 하였습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 알림 내용 해석하기
    fun translateNotification(type: Int): Pair<ImageVector, String> {
        return when (type) {
            0 -> Icons.Default.Favorite to "내 게시글을 좋아합니다"
            1 -> Icons.Default.FavoriteBorder to "내 댓글을 좋아합니다"
            2 -> Icons.AutoMirrored.Default.Comment to "내 게시글에 댓글을 남겼습니다"
            3 -> Icons.Default.AddComment to "내 댓글에 답글을 남겼습니다"
            else -> Icons.Default.ChatBubbleOutline to "나에게 쪽지를 보냈습니다"
        }
    }

    // 개별 알림 확인 처리하기
    fun checkNotification(notiUid: Int) {
        viewModelScope.launch {
            val user = getUserInfo()
            if (user.token.isEmpty()) {
                return@launch
            }

            checkNotificationUseCase(user.token, notiUid).collect {
                if (it is TsboardResponse.Success) {
                    loadNotifications()
                }
            }
        }
    }

    // 전체 알림 확인 처리하기
    fun checkAllNotifications(context: Context? = null) {
        viewModelScope.launch {
            val user = getUserInfo()
            if (user.token.isEmpty()) {
                return@launch
            }
            
            checkAllNotificationUseCase(user.token).collect {
                if (it is TsboardResponse.Success) {
                    loadNotifications()
                    context?.let {
                        Toast.makeText(context, "모든 알림을 확인하였습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
}
package me.domain.usecase.home

import kotlinx.coroutines.flow.flow
import me.domain.repository.TsboardNotificationRepository
import javax.inject.Inject

// 사용자에게 온 전체 알림 확인 처리하기
class CheckAllNotificationUseCase @Inject constructor(
    private val repository: TsboardNotificationRepository
) {
    operator fun invoke(token: String) = flow { emit(repository.checkAllNotifications(token)) }
}
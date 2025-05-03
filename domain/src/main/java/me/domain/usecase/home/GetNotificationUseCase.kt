package me.domain.usecase.home

import kotlinx.coroutines.flow.flow
import me.domain.repository.TsboardNotificationRepository
import javax.inject.Inject

// 사용자의 알림 목록 가져오기
class GetNotificationUseCase @Inject constructor(
    private val repository: TsboardNotificationRepository
) {
    operator fun invoke(
        token: String,
        limit: Int
    ) = flow { emit(repository.getUserNotifications(limit = limit, token = token)) }
}
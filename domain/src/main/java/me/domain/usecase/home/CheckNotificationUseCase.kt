package me.domain.usecase.home

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.common.ResponseNothing
import me.domain.repository.TsboardNotificationRepository
import me.domain.repository.TsboardResponse
import javax.inject.Inject

// 지정된 알림글을 읽음 처리하기
class CheckNotificationUseCase @Inject constructor(
    private val repository: TsboardNotificationRepository
) {
    operator fun invoke(token: String, notiUid: Int): Flow<TsboardResponse<ResponseNothing>> =
        flow {
            emit(repository.checkNotification(token, notiUid))
        }
}
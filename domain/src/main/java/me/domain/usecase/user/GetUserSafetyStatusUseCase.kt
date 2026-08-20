package me.domain.usecase.user

import kotlinx.coroutines.flow.flow
import me.domain.repository.TsboardUserChatRepository
import javax.inject.Inject

// 상대방에 대한 신고 및 차단 상태를 가져온다.
class GetUserSafetyStatusUseCase @Inject constructor(
    private val repository: TsboardUserChatRepository
) {
    operator fun invoke(targetUserUid: Int, token: String) = flow {
        emit(repository.getUserSafetyStatus(targetUserUid, token))
    }
}

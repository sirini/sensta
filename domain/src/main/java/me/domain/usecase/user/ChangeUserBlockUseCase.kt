package me.domain.usecase.user

import kotlinx.coroutines.flow.flow
import me.domain.repository.NuboUserChatRepository
import javax.inject.Inject

// 상대방 차단 상태를 변경한다.
class ChangeUserBlockUseCase @Inject constructor(
    private val repository: NuboUserChatRepository
) {
    operator fun invoke(targetUserUid: Int, blocked: Boolean, token: String) = flow {
        emit(
            if (blocked) repository.blockUser(targetUserUid, token)
            else repository.unblockUser(targetUserUid, token)
        )
    }
}

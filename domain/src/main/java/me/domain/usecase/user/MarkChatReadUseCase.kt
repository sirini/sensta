package me.domain.usecase.user

import kotlinx.coroutines.flow.flow
import me.domain.repository.NuboUserChatRepository
import javax.inject.Inject

// 현재 화면에서 확인한 상대방 메시지를 서버에 읽음 처리한다.
class MarkChatReadUseCase @Inject constructor(
    private val repository: NuboUserChatRepository
) {
    operator fun invoke(
        targetUserUid: Int,
        throughUid: Int,
        token: String
    ) = flow {
        emit(repository.markChatRead(targetUserUid, throughUid, token))
    }
}

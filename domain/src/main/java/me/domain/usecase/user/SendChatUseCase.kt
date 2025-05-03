package me.domain.usecase.user

import kotlinx.coroutines.flow.flow
import me.domain.repository.TsboardUserChatRepository
import javax.inject.Inject

// 상대방에게 메시지 보내기
class SendChatUseCase @Inject constructor(
    private val repository: TsboardUserChatRepository
) {
    operator fun invoke(
        targetUserUid: Int,
        message: String,
        token: String
    ) = flow {
        emit(
            repository.sendChatMessage(
                targetUserUid = targetUserUid,
                message = message,
                token = token
            )
        )
    }
}
package me.domain.usecase.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.user.TsboardSendChatResponse
import me.domain.repository.TsboardResponse
import me.domain.repository.TsboardUserRepository
import javax.inject.Inject

// 상대방에게 메시지 보내기
class SendChatUseCase @Inject constructor(
    private val repository: TsboardUserRepository
) {
    operator fun invoke(
        targetUserUid: Int,
        message: String,
        token: String
    ): Flow<TsboardResponse<TsboardSendChatResponse>> = flow {
        emit(
            repository.sendChatMessage(
                targetUserUid = targetUserUid,
                message = message,
                token = token
            )
        )
    }
}
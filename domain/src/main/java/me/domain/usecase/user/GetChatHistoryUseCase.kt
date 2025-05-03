package me.domain.usecase.user

import kotlinx.coroutines.flow.flow
import me.domain.repository.TsboardUserChatRepository
import javax.inject.Inject

// 채팅 내역 가져오기
class GetChatHistoryUseCase @Inject constructor(
    private val repository: TsboardUserChatRepository
) {
    operator fun invoke(
        targetUserUid: Int,
        limit: Int,
        token: String
    ) = flow {
        emit(
            repository.getChatHistory(
                targetUserUid = targetUserUid,
                limit = limit,
                token = token
            )
        )
    }
}
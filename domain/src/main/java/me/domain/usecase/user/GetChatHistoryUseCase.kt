package me.domain.usecase.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.user.TsboardChatHistoryResponse
import me.domain.repository.TsboardResponse
import me.domain.repository.TsboardUserRepository
import javax.inject.Inject

// 채팅 내역 가져오기
class GetChatHistoryUseCase @Inject constructor(
    private val repository: TsboardUserRepository
) {
    operator fun invoke(
        targetUserUid: Int,
        limit: Int,
        token: String
    ): Flow<TsboardResponse<TsboardChatHistoryResponse>> = flow {
        emit(repository.getChatHistory(targetUserUid = targetUserUid, limit = limit, token = token))
    }
}
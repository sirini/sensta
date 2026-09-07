package me.domain.usecase.user

import kotlinx.coroutines.flow.flow
import me.domain.repository.NuboUserChatRepository
import javax.inject.Inject

class GetChatThreadsUseCase @Inject constructor(
    private val repository: NuboUserChatRepository
) {
    operator fun invoke(limit: Int, token: String) = flow {
        emit(repository.getChatThreads(limit, token))
    }
}

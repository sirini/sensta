package me.domain.usecase.user

import kotlinx.coroutines.flow.flow
import me.domain.repository.NuboUserChatRepository
import javax.inject.Inject

// 사용자 또는 사용자가 올린 콘텐츠를 운영진에게 신고한다.
class ReportUserUseCase @Inject constructor(
    private val repository: NuboUserChatRepository
) {
    operator fun invoke(targetUserUid: Int, content: String, token: String) = flow {
        emit(repository.reportUser(targetUserUid, content, token))
    }
}

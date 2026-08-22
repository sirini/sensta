package me.domain.usecase.user

import kotlinx.coroutines.flow.flow
import me.domain.repository.NuboUserChatRepository
import javax.inject.Inject

// 다른 사용자의 기본 정보 가져오기
class GetOtherUserInfoUseCase @Inject constructor(
    private val repository: NuboUserChatRepository
) {
    operator fun invoke(userUid: Int) =
        flow { emit(repository.getOtherUserInfo(userUid = userUid)) }
}

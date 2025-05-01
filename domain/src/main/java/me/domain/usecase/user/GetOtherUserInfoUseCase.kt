package me.domain.usecase.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.user.TsboardOtherUserInfoResult
import me.domain.repository.TsboardResponse
import me.domain.repository.TsboardUserRepository
import javax.inject.Inject

// 다른 사용자의 기본 정보 가져오기
class GetOtherUserInfoUseCase @Inject constructor(
    private val repository: TsboardUserRepository
) {
    operator fun invoke(userUid: Int): Flow<TsboardResponse<TsboardOtherUserInfoResult>> =
        flow {
            emit(repository.getOtherUserInfo(userUid = userUid))
        }
}
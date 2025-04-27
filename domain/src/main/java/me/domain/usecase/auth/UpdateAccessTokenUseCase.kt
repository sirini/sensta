package me.domain.usecase.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.auth.TsboardUpdateAccessToken
import me.domain.repository.TsboardAuthRepository
import me.domain.repository.TsboardResponse
import javax.inject.Inject

// 액세스 토큰 업데이트
class UpdateAccessTokenUseCase @Inject constructor(
    private val repository: TsboardAuthRepository
) {
    operator fun invoke(
        userUid: Int,
        refresh: String
    ): Flow<TsboardResponse<TsboardUpdateAccessToken>> =
        flow {
            emit(repository.updateAccessToken(userUid = userUid, refresh = refresh))
        }
}
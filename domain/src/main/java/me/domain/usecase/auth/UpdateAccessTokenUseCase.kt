package me.domain.usecase.auth

import kotlinx.coroutines.flow.flow
import me.domain.repository.TsboardAuthRepository
import javax.inject.Inject

// 액세스 토큰 업데이트
class UpdateAccessTokenUseCase @Inject constructor(
    private val repository: TsboardAuthRepository
) {
    operator fun invoke(refresh: String) = flow {
        emit(repository.updateAccessToken(refresh = refresh))
    }
}

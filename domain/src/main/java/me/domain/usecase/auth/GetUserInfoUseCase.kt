package me.domain.usecase.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.auth.TsboardSigninResult
import me.domain.repository.TsboardAuthRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val repository: TsboardAuthRepository
) {
    operator fun invoke(): Flow<TsboardSigninResult> = flow {
        emit(repository.getUserInfo())
    }
}
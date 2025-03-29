package me.domain.usecase.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.auth.TsboardCheckEmail
import me.domain.repository.TsboardAuthRepository
import me.domain.repository.TsboardResponse
import javax.inject.Inject

class CheckEmailUseCase @Inject constructor(
    private val repository: TsboardAuthRepository
) {
    operator fun invoke(email: String): Flow<TsboardResponse<TsboardCheckEmail>> = flow {
        emit(repository.checkEmail(email = email))
    }
}
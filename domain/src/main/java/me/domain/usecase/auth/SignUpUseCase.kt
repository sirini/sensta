package me.domain.usecase.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.auth.TsboardSignup
import me.domain.repository.TsboardAuthRepository
import me.domain.repository.TsboardResponse
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: TsboardAuthRepository
) {
    operator fun invoke(
        id: String,
        password: String,
        name: String
    ): Flow<TsboardResponse<TsboardSignup>> = flow {
        emit(repository.signUp(id, password, name))
    }
}
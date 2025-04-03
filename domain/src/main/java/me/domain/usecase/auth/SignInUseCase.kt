package me.domain.usecase.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.auth.TsboardSignin
import me.domain.repository.TsboardAuthRepository
import me.domain.repository.TsboardResponse
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: TsboardAuthRepository
) {
    operator fun invoke(id: String, password: String): Flow<TsboardResponse<TsboardSignin>> = flow {
        emit(repository.signIn(id, password))
    }
}
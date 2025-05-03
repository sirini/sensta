package me.domain.usecase.auth

import kotlinx.coroutines.flow.flow
import me.domain.repository.TsboardAuthRepository
import javax.inject.Inject

// 로그인 하기
class SignInUseCase @Inject constructor(
    private val repository: TsboardAuthRepository
) {
    operator fun invoke(id: String, password: String) =
        flow { emit(repository.signIn(id, password)) }
}
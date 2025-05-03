package me.domain.usecase.auth

import kotlinx.coroutines.flow.flow
import me.domain.repository.TsboardAuthRepository
import javax.inject.Inject

// 회원가입 하기
class SignUpUseCase @Inject constructor(
    private val repository: TsboardAuthRepository
) {
    operator fun invoke(
        id: String,
        password: String,
        name: String
    ) = flow { emit(repository.signUp(id, password, name)) }
}
package me.domain.usecase.auth

import kotlinx.coroutines.flow.flow
import me.domain.repository.NuboAuthRepository
import javax.inject.Inject

// 이메일 중복 확인하기
class CheckEmailUseCase @Inject constructor(
    private val repository: NuboAuthRepository
) {
    operator fun invoke(email: String) = flow { emit(repository.checkEmail(email = email)) }
}

package me.domain.usecase.auth

import kotlinx.coroutines.flow.flow
import me.domain.repository.NuboAuthRepository
import javax.inject.Inject

class RequestPasswordResetUseCase @Inject constructor(
    private val repository: NuboAuthRepository
) {
    operator fun invoke(email: String) = flow { emit(repository.requestPasswordReset(email)) }
}

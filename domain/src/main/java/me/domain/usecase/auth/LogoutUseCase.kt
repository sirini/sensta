package me.domain.usecase.auth

import kotlinx.coroutines.flow.flow
import me.domain.repository.NuboAuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: NuboAuthRepository
) {
    operator fun invoke(token: String) = flow { emit(repository.logout(token)) }
}

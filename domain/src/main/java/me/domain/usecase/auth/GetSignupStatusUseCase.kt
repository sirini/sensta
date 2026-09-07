package me.domain.usecase.auth

import kotlinx.coroutines.flow.flow
import me.domain.repository.NuboAuthRepository
import javax.inject.Inject

class GetSignupStatusUseCase @Inject constructor(
    private val repository: NuboAuthRepository
) {
    operator fun invoke() = flow { emit(repository.getSignupStatus()) }
}

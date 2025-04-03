package me.domain.usecase.auth

import me.domain.repository.TsboardAuthRepository
import javax.inject.Inject

class ClearUserInfoUseCase @Inject constructor(
    private val repository: TsboardAuthRepository
) {
    suspend operator fun invoke() = repository.clearUserInfo()
}
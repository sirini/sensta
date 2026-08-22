package me.domain.usecase.auth

import me.domain.model.auth.NuboSigninResult
import me.domain.repository.NuboAuthRepository
import javax.inject.Inject

// 유저 정보 저장하기
class SaveUserInfoUseCase @Inject constructor(
    private val repository: NuboAuthRepository
) {
    suspend operator fun invoke(user: NuboSigninResult) = repository.saveUserInfo(user)
}

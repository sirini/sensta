package me.domain.usecase.auth

import me.domain.model.auth.TsboardSigninResult
import me.domain.repository.TsboardAuthRepository
import javax.inject.Inject

// 유저 정보 저장하기
class SaveUserInfoUseCase @Inject constructor(
    private val repository: TsboardAuthRepository
) {
    suspend operator fun invoke(user: TsboardSigninResult) = repository.saveUserInfo(user)
}
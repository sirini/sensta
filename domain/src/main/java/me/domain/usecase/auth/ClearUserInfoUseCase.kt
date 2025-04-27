package me.domain.usecase.auth

import me.domain.repository.TsboardAuthRepository
import javax.inject.Inject

// 로그아웃 시 가지고 있던 유저 정보를 삭제하기
class ClearUserInfoUseCase @Inject constructor(
    private val repository: TsboardAuthRepository
) {
    suspend operator fun invoke() = repository.clearUserInfo()
}
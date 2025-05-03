package me.domain.usecase.auth

import kotlinx.coroutines.flow.flow
import me.domain.repository.TsboardAuthRepository
import javax.inject.Inject

// 유저 정보 가져오기
class GetUserInfoUseCase @Inject constructor(
    private val repository: TsboardAuthRepository
) {
    operator fun invoke() = flow { emit(repository.getUserInfo()) }
}
package me.domain.usecase.auth

import kotlinx.coroutines.flow.flow
import me.domain.repository.TsboardAuthRepository
import javax.inject.Inject

// 닉네임 중복 확인하기
class CheckNameUseCase @Inject constructor(
    private val repository: TsboardAuthRepository
) {
    operator fun invoke(name: String) = flow { emit(repository.checkName(name = name)) }
}
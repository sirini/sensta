package me.domain.usecase.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.common.TsboardResponseNothing
import me.domain.repository.TsboardAuthRepository
import me.domain.repository.TsboardResponse
import javax.inject.Inject

// 이메일 중복 확인하기
class CheckEmailUseCase @Inject constructor(
    private val repository: TsboardAuthRepository
) {
    operator fun invoke(email: String): Flow<TsboardResponse<TsboardResponseNothing>> = flow {
        emit(repository.checkEmail(email = email))
    }
}
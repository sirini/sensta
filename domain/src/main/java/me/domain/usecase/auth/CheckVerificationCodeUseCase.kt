package me.domain.usecase.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.auth.TsboardVerifyCodeParam
import me.domain.model.common.TsboardResponseNothing
import me.domain.repository.TsboardAuthRepository
import me.domain.repository.TsboardResponse
import javax.inject.Inject

// 인증 코드 6자리 확인하기
class CheckVerificationCodeUseCase @Inject constructor(
    private val repository: TsboardAuthRepository
) {
    operator fun invoke(param: TsboardVerifyCodeParam): Flow<TsboardResponse<TsboardResponseNothing>> =
        flow {
            emit(repository.verifyCode(param))
        }
}
package me.domain.usecase.auth

import kotlinx.coroutines.flow.flow
import me.domain.model.auth.TsboardVerifyCodeParam
import me.domain.repository.TsboardAuthRepository
import javax.inject.Inject

// 인증 코드 6자리 확인하기
class CheckVerificationCodeUseCase @Inject constructor(
    private val repository: TsboardAuthRepository
) {
    operator fun invoke(param: TsboardVerifyCodeParam) = flow { emit(repository.verifyCode(param)) }
}
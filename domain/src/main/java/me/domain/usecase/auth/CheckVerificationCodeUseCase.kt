package me.domain.usecase.auth

import kotlinx.coroutines.flow.flow
import me.domain.model.auth.NuboVerifyCodeParam
import me.domain.repository.NuboAuthRepository
import javax.inject.Inject

// 인증 코드 6자리 확인하기
class CheckVerificationCodeUseCase @Inject constructor(
    private val repository: NuboAuthRepository
) {
    operator fun invoke(param: NuboVerifyCodeParam) = flow { emit(repository.verifyCode(param)) }
}

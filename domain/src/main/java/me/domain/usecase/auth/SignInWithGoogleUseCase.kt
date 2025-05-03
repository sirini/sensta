package me.domain.usecase.auth

import kotlinx.coroutines.flow.flow
import me.domain.repository.TsboardAuthRepository
import javax.inject.Inject

// 구글 계정으로 로그인하기
class SignInWithGoogleUseCase @Inject constructor(
    private val repository: TsboardAuthRepository
) {
    operator fun invoke(idToken: String) = flow { emit(repository.signInWithGoogle(idToken)) }
}
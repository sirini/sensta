package me.domain.usecase.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.auth.TsboardSignin
import me.domain.repository.TsboardAuthRepository
import me.domain.repository.TsboardResponse
import javax.inject.Inject

// 구글 계정으로 로그인하기
class SignInWithGoogleUseCase @Inject constructor(
    private val repository: TsboardAuthRepository
) {
    operator fun invoke(idToken: String): Flow<TsboardResponse<TsboardSignin>> = flow {
        emit(repository.signInWithGoogle(idToken))
    }
}
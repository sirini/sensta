package me.domain.usecase.auth

import kotlinx.coroutines.flow.flow
import me.domain.repository.NuboAuthRepository
import javax.inject.Inject

// 서버의 계정과 연관 데이터를 영구 삭제한다.
class DeleteAccountUseCase @Inject constructor(
    private val repository: NuboAuthRepository
) {
    operator fun invoke(token: String) = flow {
        emit(repository.deleteAccount(token))
    }
}

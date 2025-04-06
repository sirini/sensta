package me.domain.usecase.auth

import kotlinx.coroutines.flow.flow
import me.domain.model.auth.TsboardUpdateUserInfoParam
import me.domain.repository.TsboardAuthRepository
import javax.inject.Inject

class UpdateUserInfoUseCase @Inject constructor(
    private val repository: TsboardAuthRepository
) {
    operator fun invoke(param: TsboardUpdateUserInfoParam) = flow {
        emit(repository.updateUserInfo(param))
    }
}
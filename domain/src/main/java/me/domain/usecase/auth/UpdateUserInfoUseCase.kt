package me.domain.usecase.auth

import kotlinx.coroutines.flow.flow
import me.domain.model.auth.NuboUpdateUserInfoParam
import me.domain.repository.NuboAuthRepository
import javax.inject.Inject

// 유저 정보 업데이트
class UpdateUserInfoUseCase @Inject constructor(
    private val repository: NuboAuthRepository
) {
    operator fun invoke(param: NuboUpdateUserInfoParam) = flow {
        emit(repository.updateUserInfo(param))
    }
}

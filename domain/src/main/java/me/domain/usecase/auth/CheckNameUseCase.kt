package me.domain.usecase.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.common.TsboardResponseNothing
import me.domain.repository.TsboardAuthRepository
import me.domain.repository.TsboardResponse
import javax.inject.Inject

class CheckNameUseCase @Inject constructor(
    private val repository: TsboardAuthRepository
) {
    operator fun invoke(name: String): Flow<TsboardResponse<TsboardResponseNothing>> = flow {
        emit(repository.checkName(name = name))
    }
}
package me.domain.usecase.board

import kotlinx.coroutines.flow.flow
import me.domain.model.board.NuboModifyPostParam
import me.domain.repository.NuboBoardRepository
import javax.inject.Inject

class ModifyPostUseCase @Inject constructor(
    private val repository: NuboBoardRepository
) {
    operator fun invoke(param: NuboModifyPostParam) = flow {
        emit(repository.modifyPost(param))
    }
}

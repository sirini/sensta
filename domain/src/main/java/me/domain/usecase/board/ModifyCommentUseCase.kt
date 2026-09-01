package me.domain.usecase.board

import kotlinx.coroutines.flow.flow
import me.domain.model.board.NuboModifyCommentParam
import me.domain.repository.NuboBoardRepository
import javax.inject.Inject

class ModifyCommentUseCase @Inject constructor(
    private val repository: NuboBoardRepository
) {
    operator fun invoke(param: NuboModifyCommentParam) = flow {
        emit(repository.modifyComment(param))
    }
}

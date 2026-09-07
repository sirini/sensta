package me.domain.usecase.board

import kotlinx.coroutines.flow.flow
import me.domain.repository.NuboBoardRepository
import javax.inject.Inject

class GetEditorConfigUseCase @Inject constructor(
    private val repository: NuboBoardRepository
) {
    operator fun invoke(boardId: String, token: String) = flow {
        emit(repository.getEditorConfig(boardId, token))
    }
}

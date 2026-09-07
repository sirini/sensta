package me.domain.usecase.board

import kotlinx.coroutines.flow.flow
import me.domain.repository.NuboBoardRepository
import javax.inject.Inject

class GetPublicUserSummaryUseCase @Inject constructor(
    private val repository: NuboBoardRepository
) {
    operator fun invoke(boardId: String, targetUserUid: Int) = flow {
        emit(repository.getPublicUserSummary(boardId, targetUserUid))
    }
}

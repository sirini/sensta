package me.domain.usecase.board

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.common.TsboardResponseNothing
import me.domain.repository.TsboardBoardRepository
import me.domain.repository.TsboardResponse
import javax.inject.Inject

// 댓글 삭제하기
class RemoveCommentUseCase @Inject constructor(
    private val repository: TsboardBoardRepository
) {
    operator fun invoke(
        boardUid: Int,
        removeTargetUid: Int,
        token: String
    ): Flow<TsboardResponse<TsboardResponseNothing>> = flow {
        emit(repository.removeComment(boardUid, removeTargetUid, token))
    }
}
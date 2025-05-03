package me.domain.usecase.board

import kotlinx.coroutines.flow.flow
import me.domain.repository.TsboardBoardRepository
import javax.inject.Inject

// 댓글 삭제하기
class RemoveCommentUseCase @Inject constructor(
    private val repository: TsboardBoardRepository
) {
    operator fun invoke(
        boardUid: Int,
        removeTargetUid: Int,
        token: String
    ) = flow { emit(repository.removeComment(boardUid, removeTargetUid, token)) }
}
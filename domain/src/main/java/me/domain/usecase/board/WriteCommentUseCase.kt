package me.domain.usecase.board

import kotlinx.coroutines.flow.flow
import me.domain.model.board.TsboardWriteCommentParam
import me.domain.repository.TsboardBoardRepository
import javax.inject.Inject

// 댓글 작성하기
class WriteCommentUseCase @Inject constructor(
    private val repository: TsboardBoardRepository
) {
    operator fun invoke(
        boardUid: Int,
        postUid: Int,
        content: String,
        token: String
    ) = flow {
        emit(repository.writeComment(TsboardWriteCommentParam(boardUid, postUid, content, token)))
    }
}
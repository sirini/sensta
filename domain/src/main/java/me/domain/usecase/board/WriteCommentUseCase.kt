package me.domain.usecase.board

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.board.TsboardCommentWriteResponse
import me.domain.repository.TsboardBoardRepository
import me.domain.repository.TsboardResponse
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
    ): Flow<TsboardResponse<TsboardCommentWriteResponse>> = flow {
        emit(repository.writeComment(boardUid, postUid, content, token))
    }
}
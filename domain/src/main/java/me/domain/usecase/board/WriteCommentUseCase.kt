package me.domain.usecase.board

import kotlinx.coroutines.flow.flow
import me.domain.model.board.NuboWriteCommentParam
import me.domain.repository.NuboBoardRepository
import javax.inject.Inject

// 댓글 작성하기
class WriteCommentUseCase @Inject constructor(
    private val repository: NuboBoardRepository
) {
    operator fun invoke(
        boardUid: Int,
        postUid: Int,
        content: String,
        token: String,
        replyTargetUid: Int? = null
    ) = flow {
        emit(
            repository.writeComment(
                NuboWriteCommentParam(
                    boardUid = boardUid,
                    postUid = postUid,
                    content = content,
                    token = token,
                    replyTargetUid = replyTargetUid
                )
            )
        )
    }
}

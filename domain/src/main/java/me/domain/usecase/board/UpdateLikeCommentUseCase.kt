package me.domain.usecase.board

import kotlinx.coroutines.flow.flow
import me.domain.model.board.TsboardUpdateLikeParam
import me.domain.repository.TsboardBoardRepository
import javax.inject.Inject

// 댓글에 좋아요 클릭하기
class UpdateLikeCommentUseCase @Inject constructor(
    private val repository: TsboardBoardRepository
) {
    operator fun invoke(
        boardUid: Int,
        commentUid: Int,
        liked: Boolean,
        token: String
    ) = flow {
        emit(
            repository.updateLikeComment(
                TsboardUpdateLikeParam(
                    boardUid = boardUid,
                    targetUid = commentUid,
                    liked = if (liked) 1 else 0,
                    token = token
                )
            )
        )
    }
}
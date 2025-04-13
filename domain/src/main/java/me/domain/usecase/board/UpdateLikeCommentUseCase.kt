package me.domain.usecase.board

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.common.TsboardResponseNothing
import me.domain.repository.TsboardBoardRepository
import me.domain.repository.TsboardResponse
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
    ): Flow<TsboardResponse<TsboardResponseNothing>> = flow {
        emit(
            repository.updateLikeComment(
                boardUid = boardUid,
                commentUid = commentUid,
                liked = if (liked) 1 else 0,
                token = token
            )
        )
    }
}
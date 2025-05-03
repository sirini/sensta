package me.domain.usecase.board

import kotlinx.coroutines.flow.flow
import me.domain.model.board.TsboardUpdateLikeParam
import me.domain.repository.TsboardBoardRepository
import javax.inject.Inject

// 게시글에 좋아요 누르기
class UpdateLikePostUseCase @Inject constructor(
    private val repository: TsboardBoardRepository
) {
    operator fun invoke(
        boardUid: Int,
        postUid: Int,
        liked: Boolean,
        token: String
    ) = flow {
        emit(
            repository.updateLikePost(
                TsboardUpdateLikeParam(
                    boardUid = boardUid,
                    targetUid = postUid,
                    liked = if (liked) 1 else 0,
                    token = token
                )
            )
        )
    }
}
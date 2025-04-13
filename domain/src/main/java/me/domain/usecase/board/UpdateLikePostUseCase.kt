package me.domain.usecase.board

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.common.TsboardResponseNothing
import me.domain.repository.TsboardBoardRepository
import me.domain.repository.TsboardResponse
import javax.inject.Inject

// 좋아요 누르기
class UpdateLikePostUseCase @Inject constructor(
    private val repository: TsboardBoardRepository
) {
    operator fun invoke(
        boardUid: Int,
        postUid: Int,
        liked: Boolean,
        token: String
    ): Flow<TsboardResponse<TsboardResponseNothing>> = flow {
        emit(
            repository.updateLikePost(
                boardUid = boardUid,
                postUid = postUid,
                liked = if (liked) 1 else 0,
                token = token
            )
        )
    }
}
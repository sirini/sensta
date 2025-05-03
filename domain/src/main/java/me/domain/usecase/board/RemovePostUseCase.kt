package me.domain.usecase.board

import kotlinx.coroutines.flow.flow
import me.domain.repository.TsboardBoardRepository
import javax.inject.Inject

// 게시글 삭제하기
class RemovePostUseCase @Inject constructor(
    private val repository: TsboardBoardRepository
) {
    operator fun invoke(boardUid: Int, postUid: Int, token: String) = flow {
        emit(repository.removePost(boardUid, postUid, token))
    }
}
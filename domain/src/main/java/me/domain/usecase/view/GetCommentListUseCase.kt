package me.domain.usecase.view

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.board.TsboardComment
import me.domain.repository.TsboardBoardRepository
import me.domain.repository.TsboardResponse
import javax.inject.Inject

// 댓글 목록 가져오기
class GetCommentListUseCase @Inject constructor(
    private val repository: TsboardBoardRepository
) {
    operator fun invoke(postUid: Int, token: String): Flow<TsboardResponse<List<TsboardComment>>> =
        flow {
            emit(repository.getComments(postUid = postUid, token = token))
        }
}
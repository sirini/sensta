package me.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.board.TsboardPost
import me.domain.repository.TsboardBoardRepository
import me.domain.repository.TsboardResponse
import javax.inject.Inject

// 게시글 목록 가져오기
class GetPostListUseCase @Inject constructor(
    private val repository: TsboardBoardRepository
) {
    operator fun invoke(
        sinceUid: Int,
        option: Int,
        keyword: String
    ): Flow<TsboardResponse<List<TsboardPost>>> = flow {
        emit(repository.getPosts(sinceUid = sinceUid, option = option, keyword = keyword))
    }
}
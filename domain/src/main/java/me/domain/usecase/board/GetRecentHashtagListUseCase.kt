package me.domain.usecase.board

import kotlinx.coroutines.flow.flow
import me.domain.repository.TsboardBoardRepository
import javax.inject.Inject

// 최근 사용된 해시태그들의 목록 가져오기
class GetRecentHashtagListUseCase @Inject constructor(
    private val repository: TsboardBoardRepository
) {
    operator fun invoke(boardUid: Int, limit: Int) = flow {
        emit(repository.getRecentHashtags(boardUid, limit))
    }
}
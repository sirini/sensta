package me.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.home.TsboardLatestPost
import me.domain.repository.TsboardBoardRepository
import me.domain.repository.TsboardResponse
import javax.inject.Inject

// 지정된 게시판의 최근 게시글들 가져오기
class GetHomeLatestPostUseCase @Inject constructor(
    private val repository: TsboardBoardRepository
) {
    operator fun invoke(limit: Int): Flow<TsboardResponse<List<TsboardLatestPost>>> = flow {
        emit(repository.getHomeLatestPosts(limit))
    }
}
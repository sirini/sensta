package me.domain.usecase.home

import kotlinx.coroutines.flow.flow
import me.domain.repository.TsboardBoardRepository
import javax.inject.Inject

// 지정된 게시판의 최근 게시글들 가져오기
class GetHomeLatestPostUseCase @Inject constructor(
    private val repository: TsboardBoardRepository
) {
    operator fun invoke(limit: Int) = flow { emit(repository.getHomeLatestPosts(limit)) }
}
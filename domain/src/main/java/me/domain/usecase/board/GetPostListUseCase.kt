package me.domain.usecase.board

import kotlinx.coroutines.flow.flow
import me.domain.model.board.NuboGetPostsParam
import me.domain.repository.NuboBoardRepository
import javax.inject.Inject

// 게시글 목록 가져오기
class GetPostListUseCase @Inject constructor(
    private val repository: NuboBoardRepository
) {
    operator fun invoke(
        page: Int,
        option: Int,
        keyword: String,
        token: String
    ) = flow {
        emit(
            repository.getPosts(
                NuboGetPostsParam(
                    page = page,
                    option = option,
                    keyword = keyword,
                    token = token
                )
            )
        )
    }
}

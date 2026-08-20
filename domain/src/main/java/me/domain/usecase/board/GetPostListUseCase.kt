package me.domain.usecase.board

import kotlinx.coroutines.flow.flow
import me.domain.model.board.TsboardGetPostsParam
import me.domain.repository.TsboardBoardRepository
import javax.inject.Inject

// 게시글 목록 가져오기
class GetPostListUseCase @Inject constructor(
    private val repository: TsboardBoardRepository
) {
    operator fun invoke(
        page: Int,
        option: Int,
        keyword: String,
        token: String
    ) = flow {
        emit(
            repository.getPosts(
                TsboardGetPostsParam(
                    page = page,
                    option = option,
                    keyword = keyword,
                    token = token
                )
            )
        )
    }
}

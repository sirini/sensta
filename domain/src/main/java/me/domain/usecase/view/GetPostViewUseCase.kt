package me.domain.usecase.view

import kotlinx.coroutines.flow.flow
import me.domain.repository.TsboardBoardRepository
import javax.inject.Inject

// 게시글 내용 및 연관 정보들 가져오기
class GetPostViewUseCase @Inject constructor(
    private val repository: TsboardBoardRepository
) {
    operator fun invoke(
        postUid: Int,
        token: String,
        needUpdateHit: Boolean = false
    ) = flow {
        emit(repository.getPost(postUid = postUid, token = token, needUpdateHit = needUpdateHit))
    }
}
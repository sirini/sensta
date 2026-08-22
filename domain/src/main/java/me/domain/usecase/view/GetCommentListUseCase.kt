package me.domain.usecase.view

import kotlinx.coroutines.flow.flow
import me.domain.repository.NuboBoardRepository
import javax.inject.Inject

// 댓글 목록 가져오기
class GetCommentListUseCase @Inject constructor(
    private val repository: NuboBoardRepository
) {
    operator fun invoke(postUid: Int, token: String) =
        flow { emit(repository.getComments(postUid = postUid, token = token)) }
}

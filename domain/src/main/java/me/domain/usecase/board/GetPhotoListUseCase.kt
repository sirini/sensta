package me.domain.usecase.board

import kotlinx.coroutines.flow.flow
import me.domain.repository.TsboardBoardRepository
import javax.inject.Inject

// 갤러리 사진 목록 가져오기
class GetPhotoListUseCase @Inject constructor(
    private val repository: TsboardBoardRepository
) {
    operator fun invoke(
        sinceUid: Int = 0,
        token: String
    ) = flow { emit(repository.getPhotos(sinceUid = sinceUid, token = token)) }
}
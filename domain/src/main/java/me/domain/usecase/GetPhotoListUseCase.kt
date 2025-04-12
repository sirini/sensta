package me.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.photo.TsboardPhoto
import me.domain.repository.TsboardBoardRepository
import me.domain.repository.TsboardResponse
import javax.inject.Inject

// 갤러리 사진 목록 가져오기
class GetPhotoListUseCase @Inject constructor(
    private val repository: TsboardBoardRepository
) {
    operator fun invoke(
        sinceUid: Int = 0,
        token: String
    ): Flow<TsboardResponse<List<TsboardPhoto>>> = flow {
        emit(repository.getPhotos(sinceUid = sinceUid, token = token))
    }
}
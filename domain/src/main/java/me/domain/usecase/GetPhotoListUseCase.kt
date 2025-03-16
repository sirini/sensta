package me.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.domain.model.gallery.TsboardPhoto
import me.domain.repository.BoardRepository
import me.domain.repository.TsboardResponse
import javax.inject.Inject

// 갤러리 사진 목록 가져오기
class GetPhotoListUseCase @Inject constructor(
    private val repository: BoardRepository
) {
    operator fun invoke(): Flow<TsboardResponse<List<TsboardPhoto>>> = flow {
        emit(repository.getPhotos(sinceUid = 0))
    }
}
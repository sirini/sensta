package me.data.repository

import me.data.env.Env
import me.data.remote.api.PostApi
import me.data.remote.dto.toEntity
import me.domain.model.board.TsboardPost
import me.domain.model.gallery.TsboardPhoto
import me.domain.repository.BoardRepository
import me.domain.repository.TsboardResponse
import javax.inject.Inject

class BoardRepositoryImpl @Inject constructor(
    private val api: PostApi
) : BoardRepository {

    // 게시글 목록 가져오기 (DTO -> 엔티티 변환)
    override suspend fun getPosts(sinceUid: Int): TsboardResponse<List<TsboardPost>> {
        return try {
            val response = api.getPosts(
                id = Env.boardId,
                page = 1,
                pagingDirection = 1,
                sinceUid = sinceUid,
                option = 0
            )
            TsboardResponse.Success(response.toEntity().result.posts)
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 갤러리 사진 목록 가져오기 (DTO -> 엔티티 변환)
    override suspend fun getPhotos(sinceUid: Int): TsboardResponse<List<TsboardPhoto>> {
        return try {
            val response = api.getPhotos(
                id = Env.boardId,
                page = 1,
                pagingDirection = 1,
                sinceUid = sinceUid,
                option = 0
            )
            TsboardResponse.Success(response.toEntity().result.images)
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }
}
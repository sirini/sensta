package me.domain.repository

import me.domain.model.board.TsboardComment
import me.domain.model.board.TsboardPost
import me.domain.model.photo.TsboardPhoto

// 게시글 관련 인터페이스
interface TsboardBoardRepository {
    suspend fun getPosts(sinceUid: Int): TsboardResponse<List<TsboardPost>>
    suspend fun getPhotos(sinceUid: Int): TsboardResponse<List<TsboardPhoto>>
    suspend fun getComments(postUid: Int): TsboardResponse<List<TsboardComment>>
}
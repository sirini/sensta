package me.domain.repository

import me.domain.model.board.TsboardPost
import me.domain.model.gallery.TsboardPhoto

// 게시글 관련 인터페이스
interface BoardRepository {
    suspend fun getPosts(sinceUid: Int): TsboardResponse<List<TsboardPost>>
    suspend fun getPhotos(sinceUid: Int): TsboardResponse<List<TsboardPhoto>>
}
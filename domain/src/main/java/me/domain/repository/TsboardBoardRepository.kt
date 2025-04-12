package me.domain.repository

import me.domain.model.board.TsboardBoardViewResponse
import me.domain.model.board.TsboardComment
import me.domain.model.board.TsboardPost
import me.domain.model.home.TsboardLatestPost
import me.domain.model.photo.TsboardPhoto

// 게시글 관련 인터페이스
interface TsboardBoardRepository {
    suspend fun getPosts(
        sinceUid: Int,
        option: Int,
        keyword: String
    ): TsboardResponse<List<TsboardPost>>

    suspend fun getPost(postUid: Int): TsboardResponse<TsboardBoardViewResponse>
    suspend fun getPhotos(sinceUid: Int, token: String): TsboardResponse<List<TsboardPhoto>>
    suspend fun getComments(postUid: Int): TsboardResponse<List<TsboardComment>>
    suspend fun getHomeLatestPosts(
        limit: Int,
        accessUserUid: Int = 0
    ): TsboardResponse<List<TsboardLatestPost>>
}
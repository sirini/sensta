package me.domain.repository

import me.domain.model.board.TsboardBoardViewResponse
import me.domain.model.board.TsboardComment
import me.domain.model.board.TsboardCommentWriteResponse
import me.domain.model.board.TsboardPost
import me.domain.model.common.TsboardResponseNothing
import me.domain.model.home.TsboardLatestPost
import me.domain.model.photo.TsboardPhoto

// 게시글 관련 인터페이스
interface TsboardBoardRepository {
    suspend fun getComments(postUid: Int, token: String): TsboardResponse<List<TsboardComment>>
    suspend fun getHomeLatestPosts(
        limit: Int,
        accessUserUid: Int = 0
    ): TsboardResponse<List<TsboardLatestPost>>

    suspend fun getPosts(
        sinceUid: Int,
        option: Int,
        keyword: String,
        token: String
    ): TsboardResponse<List<TsboardPost>>

    suspend fun getPost(postUid: Int, token: String): TsboardResponse<TsboardBoardViewResponse>
    suspend fun getPhotos(sinceUid: Int, token: String): TsboardResponse<List<TsboardPhoto>>
    suspend fun removeComment(
        boardUid: Int,
        removeTargetUid: Int,
        token: String
    ): TsboardResponse<TsboardResponseNothing>

    suspend fun updateLikePost(
        boardUid: Int,
        postUid: Int,
        liked: Int,
        token: String
    ): TsboardResponse<TsboardResponseNothing>

    suspend fun updateLikeComment(
        boardUid: Int,
        commentUid: Int,
        liked: Int,
        token: String
    ): TsboardResponse<TsboardResponseNothing>

    suspend fun writeComment(
        boardUid: Int,
        postUid: Int,
        content: String,
        token: String
    ): TsboardResponse<TsboardCommentWriteResponse>
}
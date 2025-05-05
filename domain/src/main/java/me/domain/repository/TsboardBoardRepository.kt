package me.domain.repository

import me.domain.model.board.TsboardBoardViewResponse
import me.domain.model.board.TsboardComment
import me.domain.model.board.TsboardGetPostsParam
import me.domain.model.board.TsboardPost
import me.domain.model.board.TsboardRecentHashtagResponse
import me.domain.model.board.TsboardUpdateLikeParam
import me.domain.model.board.TsboardWriteCommentParam
import me.domain.model.board.TsboardWritePostParam
import me.domain.model.board.TsboardWriteResponse
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

    suspend fun getPosts(param: TsboardGetPostsParam): TsboardResponse<List<TsboardPost>>

    suspend fun getPost(
        postUid: Int,
        token: String,
        needUpdateHit: Boolean = false
    ): TsboardResponse<TsboardBoardViewResponse>

    suspend fun getPhotos(sinceUid: Int, token: String): TsboardResponse<List<TsboardPhoto>>
    suspend fun getRecentHashtags(
        boardUid: Int,
        limit: Int
    ): TsboardResponse<TsboardRecentHashtagResponse>

    suspend fun removeComment(
        boardUid: Int,
        removeTargetUid: Int,
        token: String
    ): TsboardResponse<TsboardResponseNothing>

    suspend fun removePost(
        boardUid: Int,
        postUid: Int,
        token: String
    ): TsboardResponse<TsboardResponseNothing>

    suspend fun updateLikePost(param: TsboardUpdateLikeParam): TsboardResponse<TsboardResponseNothing>
    suspend fun updateLikeComment(param: TsboardUpdateLikeParam): TsboardResponse<TsboardResponseNothing>
    suspend fun writeComment(param: TsboardWriteCommentParam): TsboardResponse<TsboardWriteResponse>
    suspend fun writePost(param: TsboardWritePostParam): TsboardResponse<TsboardWriteResponse>
}
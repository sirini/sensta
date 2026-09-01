package me.domain.repository

import me.domain.model.board.NuboBoardViewResponse
import me.domain.model.board.NuboComment
import me.domain.model.board.NuboGetPostsParam
import me.domain.model.board.NuboModifyCommentParam
import me.domain.model.board.NuboModifyPostParam
import me.domain.model.board.NuboPost
import me.domain.model.board.NuboRecentHashtagResponse
import me.domain.model.board.NuboStudio
import me.domain.model.board.NuboStudioParam
import me.domain.model.board.NuboUpdateLikeParam
import me.domain.model.board.NuboWriteCommentParam
import me.domain.model.board.NuboWritePostParam
import me.domain.model.board.NuboWriteResponse
import me.domain.model.common.NuboResponseNothing
import me.domain.model.home.NuboLatestPost

// 게시글 관련 인터페이스
interface NuboBoardRepository {
    suspend fun getMyStudio(param: NuboStudioParam): NuboResponse<NuboStudio>
    suspend fun getComments(postUid: Int, token: String): NuboResponse<List<NuboComment>>
    suspend fun getHomeLatestPosts(
        limit: Int,
        token: String = ""
    ): NuboResponse<List<NuboLatestPost>>

    suspend fun getPosts(param: NuboGetPostsParam): NuboResponse<List<NuboPost>>

    suspend fun getPost(
        postUid: Int,
        token: String,
        needUpdateHit: Boolean = false
    ): NuboResponse<NuboBoardViewResponse>

    suspend fun getRecentHashtags(
        boardUid: Int,
        limit: Int
    ): NuboResponse<NuboRecentHashtagResponse>

    suspend fun removeComment(
        boardUid: Int,
        removeTargetUid: Int,
        token: String
    ): NuboResponse<NuboResponseNothing>

    suspend fun removePost(
        boardUid: Int,
        postUid: Int,
        token: String
    ): NuboResponse<NuboResponseNothing>

    suspend fun modifyPost(param: NuboModifyPostParam): NuboResponse<NuboResponseNothing>
    suspend fun modifyComment(param: NuboModifyCommentParam): NuboResponse<NuboResponseNothing>

    suspend fun updateLikePost(param: NuboUpdateLikeParam): NuboResponse<NuboResponseNothing>
    suspend fun updateLikeComment(param: NuboUpdateLikeParam): NuboResponse<NuboResponseNothing>
    suspend fun writeComment(param: NuboWriteCommentParam): NuboResponse<NuboWriteResponse>
    suspend fun writePost(param: NuboWritePostParam): NuboResponse<NuboWriteResponse>
}

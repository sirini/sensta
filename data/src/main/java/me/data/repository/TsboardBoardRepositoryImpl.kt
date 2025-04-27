package me.data.repository

import me.data.env.Env
import me.data.remote.api.TsboardGoapi
import me.data.remote.dto.board.toEntity
import me.data.remote.dto.common.toEntity
import me.data.remote.dto.home.toEntity
import me.data.remote.dto.photo.toEntity
import me.domain.model.board.TsboardBoardViewResponse
import me.domain.model.board.TsboardComment
import me.domain.model.board.TsboardCommentWriteResponse
import me.domain.model.board.TsboardPost
import me.domain.model.common.TsboardResponseNothing
import me.domain.model.home.TsboardLatestPost
import me.domain.model.photo.TsboardPhoto
import me.domain.repository.TsboardBoardRepository
import me.domain.repository.TsboardResponse
import javax.inject.Inject

class TsboardBoardRepositoryImpl @Inject constructor(
    private val api: TsboardGoapi
) : TsboardBoardRepository {

    // 게시글에 달린 댓글 목록 가져오기
    override suspend fun getComments(
        postUid: Int,
        token: String
    ): TsboardResponse<List<TsboardComment>> {
        return try {
            val response = api.getComments(
                authorization = "Bearer $token",
                id = Env.boardId,
                postUid = postUid,
                page = 1,
                pagingDirection = 1,
                sinceUid = 0,
                bunch = 100
            )
            TsboardResponse.Success(response.toEntity().result.comments)
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 지정된 게시판의 최근글 목록 가져오기
    override suspend fun getHomeLatestPosts(
        limit: Int,
        accessUserUid: Int
    ): TsboardResponse<List<TsboardLatestPost>> {
        return try {
            val response = api.getHomeLatestPosts(
                id = Env.boardId,
                limit = limit,
                accessUserUid = accessUserUid
            )
            TsboardResponse.Success(response.toEntity().result.items)
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 게시글 목록 가져오기
    override suspend fun getPosts(
        sinceUid: Int, option: Int, keyword: String, token: String
    ): TsboardResponse<List<TsboardPost>> {
        return try {
            val response = api.getPosts(
                authorization = "Bearer $token",
                id = Env.boardId,
                page = 1,
                pagingDirection = 1,
                sinceUid = sinceUid,
                option = option,
                keyword = keyword
            )
            TsboardResponse.Success(response.toEntity().result.posts)
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 게시글 내용 가져오기
    override suspend fun getPost(
        postUid: Int,
        token: String
    ): TsboardResponse<TsboardBoardViewResponse> {
        return try {
            val response = api.getPost(
                authorization = "Bearer $token",
                id = Env.boardId,
                postUid = postUid,
                needUpdateHit = 1,
                latestLimit = 3
            )
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 갤러리 사진 목록 가져오기
    override suspend fun getPhotos(
        sinceUid: Int,
        token: String
    ): TsboardResponse<List<TsboardPhoto>> {
        return try {
            val response = api.getPhotos(
                authorization = "Bearer $token",
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

    // 댓글 삭제하기
    override suspend fun removeComment(
        boardUid: Int,
        removeTargetUid: Int,
        token: String
    ): TsboardResponse<TsboardResponseNothing> {
        return try {
            val response = api.removeComment(
                authorization = "Bearer $token",
                boardUid = boardUid,
                removeTargetUid = removeTargetUid
            )
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 게시글에 대한 좋아요 업데이트
    override suspend fun updateLikePost(
        boardUid: Int,
        postUid: Int,
        liked: Int,
        token: String
    ): TsboardResponse<TsboardResponseNothing> {
        return try {
            val response = api.likePost(
                authorization = "Bearer $token",
                boardUid = boardUid,
                postUid = postUid,
                liked = liked
            )
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 댓글에 대한 좋아요 업데이트
    override suspend fun updateLikeComment(
        boardUid: Int,
        commentUid: Int,
        liked: Int,
        token: String
    ): TsboardResponse<TsboardResponseNothing> {
        return try {
            val response = api.likeComment(
                authorization = "Bearer $token",
                boardUid = boardUid,
                commentUid = commentUid,
                liked = liked
            )
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 댓글 작성하기
    override suspend fun writeComment(
        boardUid: Int,
        postUid: Int,
        content: String,
        token: String
    ): TsboardResponse<TsboardCommentWriteResponse> {
        return try {
            val response = api.writeComment(
                authorization = "Bearer $token",
                boardUid = boardUid,
                postUid = postUid,
                content = content
            )
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }
}
package me.data.repository

import me.data.env.Env
import me.data.remote.api.TsboardGoapi
import me.data.remote.dto.board.toEntity
import me.data.remote.dto.common.toEntity
import me.data.remote.dto.home.toEntity
import me.data.remote.dto.photo.toEntity
import me.data.util.Upload
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
import me.domain.repository.TsboardBoardRepository
import me.domain.repository.TsboardResponse
import okhttp3.RequestBody.Companion.toRequestBody
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
                id = Env.BOARD_ID,
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
                id = Env.BOARD_ID,
                limit = limit,
                accessUserUid = accessUserUid
            )
            TsboardResponse.Success(response.toEntity().result.items)
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 게시글 목록 가져오기
    override suspend fun getPosts(param: TsboardGetPostsParam): TsboardResponse<List<TsboardPost>> {
        return try {
            val response = api.getPosts(
                authorization = "Bearer ${param.token}",
                id = Env.BOARD_ID,
                page = 1,
                pagingDirection = 1,
                sinceUid = param.sinceUid,
                option = param.option,
                keyword = param.keyword
            )
            TsboardResponse.Success(response.toEntity().result.posts)
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 게시글 내용 가져오기
    override suspend fun getPost(
        postUid: Int,
        token: String,
        needUpdateHit: Boolean
    ): TsboardResponse<TsboardBoardViewResponse> {
        return try {
            val response = api.getPost(
                authorization = "Bearer $token",
                id = Env.BOARD_ID,
                postUid = postUid,
                needUpdateHit = if (needUpdateHit) 1 else 0,
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
                id = Env.BOARD_ID,
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

    // 최근 사용된 해시태그들 목록 가져오기
    override suspend fun getRecentHashtags(
        boardUid: Int,
        limit: Int
    ): TsboardResponse<TsboardRecentHashtagResponse> {
        return try {
            val response = api.getRecentHashtags(
                boardUid = boardUid,
                limit = limit
            )
            TsboardResponse.Success(response.toEntity())
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

    // 게시글 삭제하기
    override suspend fun removePost(
        boardUid: Int,
        postUid: Int,
        token: String
    ): TsboardResponse<TsboardResponseNothing> {
        return try {
            val response = api.removePost(
                authorization = "Bearer $token",
                boardUid = boardUid,
                postUid = postUid
            )
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 게시글에 대한 좋아요 업데이트
    override suspend fun updateLikePost(param: TsboardUpdateLikeParam): TsboardResponse<TsboardResponseNothing> {
        return try {
            val response = api.likePost(
                authorization = "Bearer ${param.token}",
                boardUid = param.boardUid,
                postUid = param.targetUid,
                liked = param.liked
            )
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 댓글에 대한 좋아요 업데이트
    override suspend fun updateLikeComment(param: TsboardUpdateLikeParam): TsboardResponse<TsboardResponseNothing> {
        return try {
            val response = api.likeComment(
                authorization = "Bearer ${param.token}",
                boardUid = param.boardUid,
                commentUid = param.targetUid,
                liked = param.liked
            )
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 댓글 작성하기
    override suspend fun writeComment(param: TsboardWriteCommentParam): TsboardResponse<TsboardWriteResponse> {
        return try {
            val response = api.writeComment(
                authorization = "Bearer ${param.token}",
                boardUid = param.boardUid,
                postUid = param.postUid,
                content = param.content
            )
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 게시글 작성하기
    override suspend fun writePost(param: TsboardWritePostParam): TsboardResponse<TsboardWriteResponse> {
        val tagString = param.tags.joinToString(",")
        val attachmentsMultipart = param.attachments.map { uri ->
            Upload.uriToMultipart(context = param.context, uri = uri, name = "attachments[]")
                ?: return TsboardResponse.Error("사진 업로드에 실패했습니다")
        }

        val boardUidBody = param.boardUid.toString().toRequestBody()
        val categoryUidBody = param.categoryUid.toString().toRequestBody()
        val isNoticeBody = if (param.isNotice) "1".toRequestBody() else "0".toRequestBody()
        val isSecretBody = if (param.isSecret) "1".toRequestBody() else "0".toRequestBody()
        val titleBody = param.title.toRequestBody()
        val contentBody = param.content.toRequestBody()
        val tagsBody = tagString.toRequestBody()

        return try {
            val response = api.writePost(
                authorization = "Bearer ${param.token}",
                boardUid = boardUidBody,
                categoryUid = categoryUidBody,
                isNotice = isNoticeBody,
                isSecret = isSecretBody,
                title = titleBody,
                content = contentBody,
                tags = tagsBody,
                attachments = attachmentsMultipart
            )
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }
}
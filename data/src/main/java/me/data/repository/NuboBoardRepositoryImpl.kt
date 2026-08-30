package me.data.repository

import me.data.env.Env
import me.data.remote.api.NuboBoardApi
import me.data.remote.dto.board.BoardLikeRequestDto
import me.data.remote.dto.board.CommentLikeRequestDto
import me.data.remote.dto.board.RemovePostRequestDto
import me.data.remote.dto.board.toEntity
import me.data.remote.dto.common.toEntity
import me.data.remote.dto.home.toEntity
import me.data.util.Upload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.domain.model.board.NuboBoardViewResponse
import me.domain.model.board.NuboComment
import me.domain.model.board.NuboGetPostsParam
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
import me.domain.repository.NuboBoardRepository
import me.domain.repository.NuboResponse
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class NuboBoardRepositoryImpl @Inject constructor(
    private val api: NuboBoardApi
) : NuboBoardRepository {

    // 로그인한 사용자의 작품과 누적 성과 가져오기
    override suspend fun getMyStudio(param: NuboStudioParam): NuboResponse<NuboStudio> {
        return try {
            val response = api.getMyStudio(
                authorization = param.token.toAuthorizationHeader(),
                id = Env.BOARD_ID,
                page = param.page,
                limit = param.limit,
                sort = param.sort.queryValue
            )
            if (!response.success || response.result == null) {
                NuboResponse.Error(response.error.ifBlank { "작품 정보를 불러오지 못했습니다" })
            } else {
                NuboResponse.Success(response.toEntity())
            }
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    // 게시글에 달린 댓글 목록 가져오기
    override suspend fun getComments(
        postUid: Int,
        token: String
    ): NuboResponse<List<NuboComment>> {
        return try {
            val response = api.getComments(
                authorization = token.toAuthorizationHeader(),
                boardUid = Env.BOARD_UID,
                postUid = postUid,
                page = 1,
                limit = 100
            )
            NuboResponse.Success(response.toEntity().result.comments)
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    // 지정된 게시판의 최근글 목록 가져오기
    override suspend fun getHomeLatestPosts(
        limit: Int,
        token: String
    ): NuboResponse<List<NuboLatestPost>> {
        return try {
            val response = api.getHomeLatestPosts(
                authorization = token.toAuthorizationHeader(),
                id = Env.BOARD_ID,
                limit = limit
            )
            NuboResponse.Success(response.toEntity().result.items)
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    // 게시글 목록 가져오기
    override suspend fun getPosts(param: NuboGetPostsParam): NuboResponse<List<NuboPost>> {
        return try {
            val response = api.getPosts(
                authorization = param.token.toAuthorizationHeader(),
                id = Env.BOARD_ID,
                page = param.page,
                option = param.option,
                keyword = param.keyword
            )
            val result = response.toEntity().result
            // 차단한 사용자의 콘텐츠는 홈과 탐색 어디에서도 노출하지 않는다.
            NuboResponse.Success(
                result.posts.filterNot { post -> post.writer.uid in result.blackList }
            )
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    // 게시글 내용 가져오기
    override suspend fun getPost(
        postUid: Int,
        token: String,
        needUpdateHit: Boolean
    ): NuboResponse<NuboBoardViewResponse> {
        return try {
            val response = api.getPost(
                authorization = token.toAuthorizationHeader(),
                id = Env.BOARD_ID,
                postUid = postUid,
                needUpdateHit = if (needUpdateHit) 1 else 0,
                latestLimit = 3
            )
            NuboResponse.Success(response.toEntity())
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    // 최근 사용된 해시태그들 목록 가져오기
    override suspend fun getRecentHashtags(
        boardUid: Int,
        limit: Int
    ): NuboResponse<NuboRecentHashtagResponse> {
        return try {
            val response = api.getRecentHashtags(
                boardUid = boardUid,
                limit = limit
            )
            NuboResponse.Success(response.toEntity())
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    // 댓글 삭제하기
    override suspend fun removeComment(
        boardUid: Int,
        removeTargetUid: Int,
        token: String
    ): NuboResponse<NuboResponseNothing> {
        return try {
            val response = api.removeComment(
                authorization = "Bearer $token",
                boardUid = boardUid,
                removeTargetUid = removeTargetUid
            )
            NuboResponse.Success(response.toEntity())
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    // 게시글 삭제하기
    override suspend fun removePost(
        boardUid: Int,
        postUid: Int,
        token: String
    ): NuboResponse<NuboResponseNothing> {
        return try {
            val response = api.removePost(
                authorization = "Bearer $token",
                request = RemovePostRequestDto(boardUid = boardUid, postUid = postUid)
            )
            NuboResponse.Success(response.toEntity())
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    // 게시글에 대한 좋아요 업데이트
    override suspend fun updateLikePost(param: NuboUpdateLikeParam): NuboResponse<NuboResponseNothing> {
        return try {
            val response = api.likePost(
                authorization = "Bearer ${param.token}",
                request = BoardLikeRequestDto(
                    boardUid = param.boardUid,
                    postUid = param.targetUid,
                    liked = param.liked != 0
                )
            )
            NuboResponse.Success(response.toEntity())
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    // 댓글에 대한 좋아요 업데이트
    override suspend fun updateLikeComment(param: NuboUpdateLikeParam): NuboResponse<NuboResponseNothing> {
        return try {
            val response = api.likeComment(
                authorization = "Bearer ${param.token}",
                request = CommentLikeRequestDto(
                    boardUid = param.boardUid,
                    commentUid = param.targetUid,
                    liked = param.liked != 0
                )
            )
            NuboResponse.Success(response.toEntity())
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    // 댓글 작성하기
    override suspend fun writeComment(param: NuboWriteCommentParam): NuboResponse<NuboWriteResponse> {
        return try {
            val response = api.writeComment(
                authorization = "Bearer ${param.token}",
                boardUid = param.boardUid,
                postUid = param.postUid,
                content = param.content
            )
            NuboResponse.Success(response.toEntity())
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    // 게시글 작성하기
    override suspend fun writePost(param: NuboWritePostParam): NuboResponse<NuboWriteResponse> {
        val tagString = param.tags.joinToString(",")
        val preparedFiles = mutableListOf<Upload.PreparedFile>()
        withContext(Dispatchers.IO) {
            for (uri in param.attachments) {
                val prepared = Upload.prepareImage(
                    context = param.context,
                    uri = uri,
                    name = "attachments[]"
                ) ?: break
                preparedFiles += prepared
            }
        }
        if (preparedFiles.size != param.attachments.size) {
            preparedFiles.cleanUp()
            return NuboResponse.Error("사진 업로드를 준비하지 못했습니다")
        }
        val attachmentsMultipart = preparedFiles.map { it.part }

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
            NuboResponse.Success(response.toEntity())
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        } finally {
            withContext(Dispatchers.IO) { preparedFiles.cleanUp() }
        }
    }
}

// 비로그인 요청에는 불필요한 Bearer 접두사를 보내지 않는다.
private fun String.toAuthorizationHeader() = if (isBlank()) "" else "Bearer $this"

// 업로드 완료 여부와 관계없이 앱 캐시에 복사한 임시 파일을 정리한다.
private fun List<Upload.PreparedFile>.cleanUp() = forEach { it.cleanUp() }

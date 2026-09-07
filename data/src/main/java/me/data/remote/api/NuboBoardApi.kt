package me.data.remote.api

import me.data.remote.dto.board.BoardLikeRequestDto
import me.data.remote.dto.board.BoardListResponseDto
import me.data.remote.dto.board.BoardViewResponseDto
import me.data.remote.dto.board.CommentLikeRequestDto
import me.data.remote.dto.board.CommentListResponseDto
import me.data.remote.dto.board.EditorConfigResponseDto
import me.data.remote.dto.board.ModifyCommentRequestDto
import me.data.remote.dto.board.RecentHashtagResponseDto
import me.data.remote.dto.board.PublicUserSummaryResponseDto
import me.data.remote.dto.board.RemovePostRequestDto
import me.data.remote.dto.board.StudioResponseDto
import me.data.remote.dto.board.TagSuggestionResponseDto
import me.data.remote.dto.board.WriteResponseDto
import me.data.remote.dto.common.ResponseNothingDto
import me.data.remote.dto.home.HomeLatestResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface NuboBoardApi {
    @GET("editor/config")
    suspend fun getEditorConfig(
        @Header("Authorization") authorization: String,
        @Query("id") id: String
    ): EditorConfigResponseDto

    @GET("editor/suggestion/tag")
    suspend fun getTagSuggestions(
        @Header("Authorization") authorization: String,
        @Query("tag") tag: String,
        @Query("limit") limit: Int
    ): TagSuggestionResponseDto

    @GET("board/user/summary")
    suspend fun getPublicUserSummary(
        @Query("id") id: String,
        @Query("targetUserUid") targetUserUid: Int
    ): PublicUserSummaryResponseDto

    // 로그인한 사용자의 작품과 누적 성과 가져오기
    @GET("board/my/studio")
    suspend fun getMyStudio(
        @Header("Authorization") authorization: String,
        @Query("id") id: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("sort") sort: String
    ): StudioResponseDto

    // 게시글 목록 가져오기
    @GET("board/list")
    suspend fun getPosts(
        @Header("Authorization") authorization: String,
        @Query("id") id: String,
        @Query("page") page: Int,
        @Query("option") option: Int,
        @Query("keyword") keyword: String
    ): BoardListResponseDto

    // 게시글에 좋아요 누르기
    @PATCH("board/like")
    suspend fun likePost(
        @Header("Authorization") authorization: String,
        @Body request: BoardLikeRequestDto
    ): ResponseNothingDto

    // 최근 사용된 해시태그들 목록 가져오기
    @GET("board/tag/recent")
    suspend fun getRecentHashtags(
        @Query("boardUid") boardUid: Int,
        @Query("limit") limit: Int
    ): RecentHashtagResponseDto

    // 게시글 삭제하기
    @DELETE("board/remove/post")
    suspend fun removePost(
        @Header("Authorization") authorization: String,
        @Body request: RemovePostRequestDto
    ): ResponseNothingDto

    // 게시글 수정하기. 기존 첨부 사진은 서버에서 그대로 유지한다.
    @Multipart
    @PATCH("editor/modify")
    suspend fun modifyPost(
        @Header("Authorization") authorization: String,
        @Part("boardUid") boardUid: RequestBody,
        @Part("postUid") postUid: RequestBody,
        @Part("categoryUid") categoryUid: RequestBody,
        @Part("isNotice") isNotice: RequestBody,
        @Part("isSecret") isSecret: RequestBody,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part("tags") tags: RequestBody
    ): ResponseNothingDto

    // 게시글 상세 정보 가져오기
    @GET("board/view")
    suspend fun getPost(
        @Header("Authorization") authorization: String,
        @Query("id") id: String,
        @Query("postUid") postUid: Int,
        @Query("needUpdateHit") needUpdateHit: Int,
        @Query("latestLimit") latestLimit: Int
    ): BoardViewResponseDto
    // 댓글에 좋아요 누르기
    @PATCH("comment/like")
    suspend fun likeComment(
        @Header("Authorization") authorization: String,
        @Body request: CommentLikeRequestDto
    ): ResponseNothingDto

    // 댓글 삭제하기
    @DELETE("comment/remove")
    suspend fun removeComment(
        @Header("Authorization") authorization: String,
        @Query("boardUid") boardUid: Int,
        @Query("removeTargetUid") removeTargetUid: Int
    ): ResponseNothingDto

    // 댓글 수정하기
    @PATCH("comment/modify")
    suspend fun modifyComment(
        @Header("Authorization") authorization: String,
        @Body request: ModifyCommentRequestDto
    ): ResponseNothingDto

    // 댓글 작성하기
    @FormUrlEncoded
    @POST("comment/write")
    suspend fun writeComment(
        @Header("Authorization") authorization: String,
        @Field("boardUid") boardUid: Int,
        @Field("postUid") postUid: Int,
        @Field("content") content: String
    ): WriteResponseDto

    // 기존 댓글에 답글 작성하기
    @FormUrlEncoded
    @POST("comment/reply")
    suspend fun replyComment(
        @Header("Authorization") authorization: String,
        @Field("boardUid") boardUid: Int,
        @Field("postUid") postUid: Int,
        @Field("replyTargetUid") replyTargetUid: Int,
        @Field("content") content: String
    ): WriteResponseDto

    // 댓글 목록 가져오기
    @GET("comment/list")
    suspend fun getComments(
        @Header("Authorization") authorization: String,
        @Query("boardUid") boardUid: Int,
        @Query("postUid") postUid: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): CommentListResponseDto

    // 게시글 작성하기
    @Multipart
    @POST("editor/write")
    suspend fun writePost(
        @Header("Authorization") authorization: String,
        @Part("boardUid") boardUid: RequestBody,
        @Part("categoryUid") categoryUid: RequestBody,
        @Part("isNotice") isNotice: RequestBody,
        @Part("isSecret") isSecret: RequestBody,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part("tags") tags: RequestBody,
        @Part attachments: List<MultipartBody.Part>
    ): WriteResponseDto

    // 최신글 목록 가져오기 (탐색 페이지 초기 로딩용)
    @GET("home/latest/{id}")
    suspend fun getHomeLatestPosts(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Query("limit") limit: Int
    ): HomeLatestResponseDto
}

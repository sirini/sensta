package me.data.remote.api

import me.data.remote.dto.auth.SigninDto
import me.data.remote.dto.auth.SignupDto
import me.data.remote.dto.auth.UpdateAccessTokenDto
import me.data.remote.dto.auth.UpdateUserInfoDto
import me.data.remote.dto.board.BoardListResponseDto
import me.data.remote.dto.board.BoardViewResponseDto
import me.data.remote.dto.board.CommentListResponseDto
import me.data.remote.dto.board.WriteResponseDto
import me.data.remote.dto.common.ResponseNothingDto
import me.data.remote.dto.home.HomeLatestResponseDto
import me.data.remote.dto.home.NotificationListResponseDto
import me.data.remote.dto.photo.BoardPhotoListResponseDto
import me.data.remote.dto.user.ChatHistoryListResponseDto
import me.data.remote.dto.user.OtherUserInfoDto
import me.data.remote.dto.user.SendChatResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

interface TsboardGoapi {

    // 구글 로그인 후 id_token값 전송하고 토큰 받아오기
    @FormUrlEncoded
    @POST("auth/android/google")
    suspend fun signInWithGoogle(
        @Field("id_token") idToken: String
    ): SigninDto

    // 아이디가 존재하는지 확인하기
    @FormUrlEncoded
    @POST("auth/checkemail")
    suspend fun checkID(
        @Field("email") email: String
    ): ResponseNothingDto

    // 닉네임이 존재하는지 확인하기
    @FormUrlEncoded
    @POST("auth/checkname")
    suspend fun checkName(
        @Field("name") name: String
    ): ResponseNothingDto

    // 리프레시 토큰으로 새 액세스 토큰 발급 받기
    @FormUrlEncoded
    @POST("auth/refresh")
    suspend fun updateAccessToken(
        @Field("userUid") userUid: Int,
        @Field("refresh") refresh: String
    ): UpdateAccessTokenDto

    // 로그인 하기
    @FormUrlEncoded
    @POST("auth/signin")
    suspend fun signIn(
        @Field("id") id: String,
        @Field("password") password: String
    ): SigninDto

    // 회원가입 하기
    @FormUrlEncoded
    @POST("auth/signup")
    suspend fun signUp(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("name") name: String
    ): SignupDto

    // 사용자의 정보 업데이트하기
    @Multipart
    @PATCH("auth/update")
    suspend fun updateUserInfo(
        @Header("Authorization") authorization: String,
        @Part("name") name: RequestBody,
        @Part("signature") signature: RequestBody,
        @Part("password") password: RequestBody,
        @Part profile: MultipartBody.Part?
    ): UpdateUserInfoDto

    // 회원가입 시 인증코드 확인하기
    @FormUrlEncoded
    @POST("auth/verify")
    suspend fun verifyCode(
        @Field("target") target: Int,
        @Field("code") code: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("name") name: String
    ): ResponseNothingDto

    // 게시글 목록 가져오기
    @GET("board/list")
    suspend fun getPosts(
        @Header("Authorization") authorization: String,
        @Query("id") id: String,
        @Query("page") page: Int,
        @Query("pagingDirection") pagingDirection: Int,
        @Query("sinceUid") sinceUid: Int,
        @Query("option") option: Int,
        @Query("keyword") keyword: String
    ): BoardListResponseDto

    // 게시글에 좋아요 누르기
    @PATCH("board/like")
    suspend fun likePost(
        @Header("Authorization") authorization: String,
        @Query("boardUid") boardUid: Int,
        @Query("postUid") postUid: Int,
        @Query("liked") liked: Int
    ): ResponseNothingDto

    // 갤러리 목록 가져오기
    @GET("board/photo/list")
    suspend fun getPhotos(
        @Header("Authorization") authorization: String,
        @Query("id") id: String,
        @Query("page") page: Int,
        @Query("pagingDirection") pagingDirection: Int,
        @Query("sinceUid") sinceUid: Int,
        @Query("option") option: Int
    ): BoardPhotoListResponseDto

    // 게시글 삭제하기
    @DELETE("board/remove/post")
    suspend fun removePost(
        @Header("Authorization") authorization: String,
        @Query("boardUid") boardUid: Int,
        @Query("postUid") postUid: Int
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

    // 상대방과 나눈 최근 메시지들 기록 가져오기
    @GET("chat/history")
    suspend fun getChatHistory(
        @Header("Authorization") authorization: String,
        @Query("targetUserUid") targetUserUid: Int,
        @Query("limit") limit: Int
    ): ChatHistoryListResponseDto

    // 상대방에게 메시지 보내기
    @FormUrlEncoded
    @POST("chat/save")
    suspend fun sendChatMessage(
        @Header("Authorization") authorization: String,
        @Field("targetUserUid") targetUserUid: Int,
        @Field("message") message: String
    ): SendChatResponseDto

    // 댓글에 좋아요 누르기
    @PATCH("comment/like")
    suspend fun likeComment(
        @Header("Authorization") authorization: String,
        @Query("boardUid") boardUid: Int,
        @Query("commentUid") commentUid: Int,
        @Query("liked") liked: Int
    ): ResponseNothingDto

    // 댓글 삭제하기
    @DELETE("comment/remove")
    suspend fun removeComment(
        @Header("Authorization") authorization: String,
        @Query("boardUid") boardUid: Int,
        @Query("removeTargetUid") removeTargetUid: Int
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

    // 댓글 목록 가져오기
    @GET("comment/list")
    suspend fun getComments(
        @Header("Authorization") authorization: String,
        @Query("id") id: String,
        @Query("postUid") postUid: Int,
        @Query("page") page: Int,
        @Query("pagingDirection") pagingDirection: Int,
        @Query("bunch") bunch: Int,
        @Query("sinceUid") sinceUid: Int
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
    @GET("home/latest/post")
    suspend fun getHomeLatestPosts(
        @Query("id") id: String,
        @Query("limit") limit: Int,
        @Query("accessUserUid") accessUserUid: Int
    ): HomeLatestResponseDto

    // 사용자에게 온 알림 내역 가져오기
    @GET("noti/load")
    suspend fun getUserNotifications(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int,
    ): NotificationListResponseDto

    // 사용자에게 온 개별 알림 내역 확인 처리하기
    @PATCH("noti/checked/{notiUid}")
    suspend fun checkNotification(
        @Header("Authorization") authorization: String,
        @Path("notiUid") notiUid: Int,
    ): ResponseNothingDto

    // 사용자에게 온 알림 내역 모두 확인 처리하기
    @PATCH("noti/checked")
    suspend fun checkAllNotifications(
        @Header("Authorization") authorization: String,
    ): ResponseNothingDto

    // 다른 사용자의 기본 정보 가져오기
    @GET("user/load/info")
    suspend fun getOtherUserInfo(
        @Query("targetUserUid") targetUserUid: Int
    ): OtherUserInfoDto
}
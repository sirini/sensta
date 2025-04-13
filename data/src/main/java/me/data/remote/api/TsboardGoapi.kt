package me.data.remote.api

import me.data.remote.dto.auth.CheckEmailDto
import me.data.remote.dto.auth.SigninDto
import me.data.remote.dto.auth.UpdateAccessTokenDto
import me.data.remote.dto.auth.UpdateUserInfoDto
import me.data.remote.dto.board.BoardListResponseDto
import me.data.remote.dto.board.BoardViewResponseDto
import me.data.remote.dto.board.CommentListResponseDto
import me.data.remote.dto.common.ResponseNothingDto
import me.data.remote.dto.home.HomeLatestResponseDto
import me.data.remote.dto.home.NotificationDto
import me.data.remote.dto.photo.BoardPhotoListResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    // 아이디가 존재하는지 확인하기
    @FormUrlEncoded
    @POST("auth/checkemail")
    suspend fun checkID(
        @Field("email") email: String
    ): CheckEmailDto

    // 로그인 하기
    @FormUrlEncoded
    @POST("auth/signin")
    suspend fun signIn(
        @Field("id") id: String,
        @Field("password") password: String
    ): SigninDto

    // 리프레시 토큰으로 새 액세스 토큰 발급 받기
    @FormUrlEncoded
    @POST("auth/refresh")
    suspend fun updateAccessToken(
        @Field("userUid") userUid: Int,
        @Field("refresh") refresh: String
    ): UpdateAccessTokenDto

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

    // 댓글에 좋아요 누르기
    @PATCH("comment/like")
    suspend fun likeComment(
        @Header("Authorization") authorization: String,
        @Query("boardUid") boardUid: Int,
        @Query("commentUid") commentUid: Int,
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

    // 게시글 상세 정보 가져오기
    @GET("board/view")
    suspend fun getPost(
        @Header("Authorization") authorization: String,
        @Query("id") id: String,
        @Query("postUid") postUid: Int,
        @Query("needUpdateHit") needUpdateHit: Int,
        @Query("latestLimit") latestLimit: Int
    ): BoardViewResponseDto

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
    ): NotificationDto

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
}
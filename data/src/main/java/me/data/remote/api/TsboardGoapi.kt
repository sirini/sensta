package me.data.remote.api

import me.data.remote.dto.auth.CheckEmailDto
import me.data.remote.dto.auth.SigninDto
import me.data.remote.dto.auth.UpdateAccessTokenDto
import me.data.remote.dto.board.BoardListResponseDto
import me.data.remote.dto.board.BoardViewResponseDto
import me.data.remote.dto.board.CommentListResponseDto
import me.data.remote.dto.home.HomeLatestResponseDto
import me.data.remote.dto.photo.BoardPhotoListResponseDto
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TsboardGoapi {

    // 게시글 목록 가져오기
    @GET("board/list")
    suspend fun getPosts(
        @Query("id") id: String,
        @Query("page") page: Int,
        @Query("pagingDirection") pagingDirection: Int,
        @Query("sinceUid") sinceUid: Int,
        @Query("option") option: Int,
        @Query("keyword") keyword: String
    ): BoardListResponseDto

    // 게시글 상세 정보 가져오기
    @GET("board/view")
    suspend fun getPost(
        @Query("id") id: String,
        @Query("postUid") postUid: Int,
        @Query("needUpdateHit") needUpdateHit: Int,
        @Query("latestLimit") latestLimit: Int
    ): BoardViewResponseDto

    // 갤러리 목록 가져오기
    @GET("board/photo/list")
    suspend fun getPhotos(
        @Query("id") id: String,
        @Query("page") page: Int,
        @Query("pagingDirection") pagingDirection: Int,
        @Query("sinceUid") sinceUid: Int,
        @Query("option") option: Int
    ): BoardPhotoListResponseDto

    // 댓글 목록 가져오기
    @GET("comment/list")
    suspend fun getComments(
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

    // 아이디가 존재하는지 확인하기
    @FormUrlEncoded
    @POST("auth/checkemail")
    suspend fun checkID(
        @Field("email") email: String
    ): CheckEmailDto

    // 로그인 하기
    @FormUrlEncoded
    @POST("auth/signin")
    suspend fun signin(
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
}
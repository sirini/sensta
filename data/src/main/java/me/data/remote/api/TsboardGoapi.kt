package me.data.remote.api

import me.data.remote.dto.board.BoardListResponseDto
import me.data.remote.dto.board.BoardViewResponseDto
import me.data.remote.dto.board.CommentListResponseDto
import me.data.remote.dto.home.HomeLatestResponseDto
import me.data.remote.dto.photo.BoardPhotoListResponseDto
import retrofit2.http.GET
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

}
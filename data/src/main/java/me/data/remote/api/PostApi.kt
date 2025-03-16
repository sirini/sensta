package me.data.remote.api

import me.data.remote.dto.BoardListResponseDto
import me.data.remote.dto.BoardPhotoListResponseDto
import me.data.remote.dto.PostDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PostApi {

    // 게시글 목록 가져오기
    @GET("board/list")
    suspend fun getPosts(
        @Query("id") id: String,
        @Query("page") page: Int,
        @Query("pagingDirection") pagingDirection: Int,
        @Query("sinceUid") sinceUid: Int,
        @Query("option") option: Int
    ): BoardListResponseDto

    // 게시글 상세 정보 가져오기
    @GET("board/view")
    suspend fun getPost(
        @Query("id") id: String,
        @Query("postUid") postUid: Int,
        @Query("needUpdateHit") needUpdateHit: Int,
        @Query("latestLimit") latestLimit: Int
    ): PostDto

    // 갤러리 목록 가져오기
    @GET("board/photo/list")
    suspend fun getPhotos(
        @Query("id") id: String,
        @Query("page") page: Int,
        @Query("pagingDirection") pagingDirection: Int,
        @Query("sinceUid") sinceUid: Int,
        @Query("option") option: Int
    ): BoardPhotoListResponseDto
}
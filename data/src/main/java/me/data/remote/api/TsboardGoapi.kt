package me.data.remote.api

import me.data.remote.dto.auth.SigninDto
import me.data.remote.dto.auth.SignupDto
import me.data.remote.dto.auth.UpdateAccessTokenDto
import me.data.remote.dto.auth.MobileRefreshRequestDto
import me.data.remote.dto.auth.UpdateUserInfoDto
import me.data.remote.dto.auth.DeleteAccountRequestDto
import me.data.remote.dto.board.BoardListResponseDto
import me.data.remote.dto.board.BoardLikeRequestDto
import me.data.remote.dto.board.BoardViewResponseDto
import me.data.remote.dto.board.CommentListResponseDto
import me.data.remote.dto.board.CommentLikeRequestDto
import me.data.remote.dto.board.RecentHashtagResponseDto
import me.data.remote.dto.board.RemovePostRequestDto
import me.data.remote.dto.board.WriteResponseDto
import me.data.remote.dto.common.ResponseNothingDto
import me.data.remote.dto.common.BooleanResponseDto
import me.data.remote.dto.home.HomeLatestResponseDto
import me.data.remote.dto.home.NotificationListResponseDto
import me.data.remote.dto.home.PushDeviceRequestDto
import me.data.remote.dto.user.ChatHistoryListResponseDto
import me.data.remote.dto.user.OtherUserInfoDto
import me.data.remote.dto.user.SendChatResponseDto
import me.data.remote.dto.user.SendChatRequestDto
import me.data.remote.dto.user.UserReportRequestDto
import me.data.remote.dto.user.UserSafetyStatusResponseDto
import me.data.remote.dto.user.UserTargetRequestDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Body
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
    ): BooleanResponseDto

    // 닉네임이 존재하는지 확인하기
    @FormUrlEncoded
    @POST("auth/checkname")
    suspend fun checkName(
        @Field("name") name: String
    ): BooleanResponseDto

    // 리프레시 토큰으로 새 액세스 토큰 발급 받기
    @POST("auth/android/refresh")
    suspend fun updateAccessToken(
        @Body request: MobileRefreshRequestDto
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
        @Field("id") email: String,
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
        @Field("id") email: String,
        @Field("password") password: String,
        @Field("name") name: String
    ): BooleanResponseDto

    // 계정과 계정에 연결된 모든 데이터를 영구 삭제하기
    @HTTP(method = "DELETE", path = "auth/account", hasBody = true)
    suspend fun deleteAccount(
        @Header("Authorization") authorization: String,
        @Body request: DeleteAccountRequestDto
    ): ResponseNothingDto

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
    @POST("chat/save")
    suspend fun sendChatMessage(
        @Header("Authorization") authorization: String,
        @Body request: SendChatRequestDto
    ): SendChatResponseDto

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

    // 사용자에게 온 알림 내역 가져오기
    @GET("home/noti/load")
    suspend fun getUserNotifications(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int,
    ): NotificationListResponseDto

    // 사용자에게 온 개별 알림 내역 확인 처리하기
    @PATCH("home/noti/checked/{notiUid}")
    suspend fun checkNotification(
        @Header("Authorization") authorization: String,
        @Path("notiUid") notiUid: Int,
    ): ResponseNothingDto

    // 사용자에게 온 알림 내역 모두 확인 처리하기
    @PATCH("home/noti/checked")
    suspend fun checkAllNotifications(
        @Header("Authorization") authorization: String,
    ): ResponseNothingDto

    // 현재 기기의 Firebase 푸시 토큰 등록하기
    @POST("push/device")
    suspend fun registerPushDevice(
        @Header("Authorization") authorization: String,
        @Body request: PushDeviceRequestDto
    ): ResponseNothingDto

    // 로그아웃할 기기의 Firebase 푸시 토큰 해제하기
    @HTTP(method = "DELETE", path = "push/device", hasBody = true)
    suspend fun unregisterPushDevice(
        @Header("Authorization") authorization: String,
        @Body request: PushDeviceRequestDto
    ): ResponseNothingDto

    // 다른 사용자의 기본 정보 가져오기
    @GET("auth/user/info")
    suspend fun getOtherUserInfo(
        @Query("targetUserUid") targetUserUid: Int
    ): OtherUserInfoDto

    // 상대방 신고 및 차단 상태 확인하기
    @GET("auth/user/report")
    suspend fun getUserSafetyStatus(
        @Header("Authorization") authorization: String,
        @Query("targetUserUid") targetUserUid: Int
    ): UserSafetyStatusResponseDto

    // 사용자 또는 해당 사용자가 작성한 콘텐츠 신고하기
    @POST("auth/user/report")
    suspend fun reportUser(
        @Header("Authorization") authorization: String,
        @Body request: UserReportRequestDto
    ): ResponseNothingDto

    // 사용자 차단하기
    @retrofit2.http.PUT("auth/user/block")
    suspend fun blockUser(
        @Header("Authorization") authorization: String,
        @Body request: UserTargetRequestDto
    ): ResponseNothingDto

    // 사용자 차단 해제하기
    @HTTP(method = "DELETE", path = "auth/user/block", hasBody = true)
    suspend fun unblockUser(
        @Header("Authorization") authorization: String,
        @Body request: UserTargetRequestDto
    ): ResponseNothingDto
}

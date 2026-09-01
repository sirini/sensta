package me.data.remote.dto.board

import kotlinx.serialization.Serializable

// 게시글 좋아요 변경 요청
@Serializable
data class BoardLikeRequestDto(
    val boardUid: Int,
    val postUid: Int,
    val liked: Boolean
)

// 댓글 좋아요 변경 요청
@Serializable
data class CommentLikeRequestDto(
    val boardUid: Int,
    val commentUid: Int,
    val liked: Boolean
)

// 게시글 삭제 요청
@Serializable
data class RemovePostRequestDto(
    val boardUid: Int,
    val postUid: Int
)

// 댓글 수정 요청
@Serializable
data class ModifyCommentRequestDto(
    val boardUid: Int,
    val postUid: Int,
    val modifyTargetUid: Int,
    val content: String
)

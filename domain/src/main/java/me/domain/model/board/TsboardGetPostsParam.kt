package me.domain.model.board

// 게시글들 목록 가져오기에 사용하는 파라미터
data class TsboardGetPostsParam(
    val sinceUid: Int,
    val option: Int,
    val keyword: String,
    val token: String
)
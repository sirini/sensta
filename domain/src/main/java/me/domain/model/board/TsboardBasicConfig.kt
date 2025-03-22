package me.domain.model.board

// 게시판 기본 정보 엔티티
data class TsboardBasicConfig(
    val id: String,
    val type: Int,
    val name: String
)
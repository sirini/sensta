package me.domain.model.board

// 게시판 설정 엔티티
data class TsboardConfig(
    val uid: Int,
    val id: String,
    val type: Int,
    val name: String,
    val info: String,
    val useCategory: Boolean,
    val category: List<TsboardCategory>,
    val level: TsboardLevel,
    val point: TsboardPoint
)

// 게시판 레벨 제한 엔티티
data class TsboardLevel(
    val view: Int,
    val write: Int,
    val comment: Int,
    val download: Int,
    val list: Int
)

// 게시판 포인트 증가/차감 엔티티
data class TsboardPoint(
    val view: Int,
    val write: Int,
    val comment: Int,
    val download: Int
)
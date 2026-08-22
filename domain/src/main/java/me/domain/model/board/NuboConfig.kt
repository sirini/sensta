package me.domain.model.board

// 게시판 설정 엔티티
data class NuboConfig(
    val uid: Int,
    val id: String,
    val type: Int,
    val name: String,
    val info: String,
    val useCategory: Boolean,
    val category: List<NuboCategory>,
    val level: NuboLevel,
    val point: NuboPoint
)

// 게시판 레벨 제한 엔티티
data class NuboLevel(
    val view: Int,
    val write: Int,
    val comment: Int,
    val download: Int,
    val list: Int
)

// 게시판 포인트 증가/차감 엔티티
data class NuboPoint(
    val view: Int,
    val write: Int,
    val comment: Int,
    val download: Int
)

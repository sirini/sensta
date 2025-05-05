package me.data.remote.dto.board

import kotlinx.serialization.Serializable
import me.domain.model.board.TsboardConfig
import me.domain.model.board.TsboardLevel
import me.domain.model.board.TsboardPoint

// 게시판 설정 JSON 응답 정의
@Serializable
data class ConfigDto(
    val uid: Int,
    val id: String,
    val groupUid: Int,
    val admin: AdminDto,
    val type: Int,
    val name: String,
    val info: String,
    val rowCount: Int,
    val width: Int,
    val useCategory: Boolean,
    val category: List<CategoryDto>,
    val level: LevelDto,
    val point: PointDto
)

// 그룹, 게시판 관리자 번호 JSON 응답 정의
@Serializable
data class AdminDto(
    val group: Int,
    val board: Int
)

// 게시판 레벨 제한 JSON 응답 정의
@Serializable
data class LevelDto(
    val view: Int,
    val write: Int,
    val comment: Int,
    val download: Int,
    val list: Int
)

// 게시판 포인트 증차감 JSON 응답 정의
@Serializable
data class PointDto(
    val view: Int,
    val write: Int,
    val comment: Int,
    val download: Int
)

// Config 응답을 엔티티로 변환하는 매퍼
fun ConfigDto.toEntity() = TsboardConfig(
    uid = uid,
    id = id,
    type = type,
    name = name,
    info = info,
    useCategory = useCategory,
    category = category.map { it.toEntity() },
    level = level.toEntity(),
    point = point.toEntity()
)

// Level 응답을 엔티티로 변환하는 매퍼
fun LevelDto.toEntity() = TsboardLevel(
    view = view,
    write = write,
    comment = comment,
    download = download,
    list = list
)

// Point 응답을 엔티티로 변환하는 매퍼
fun PointDto.toEntity() = TsboardPoint(
    view = view,
    write = write,
    comment = comment,
    download = download
)

package me.data.remote.dto.board

import kotlinx.serialization.Serializable
import me.domain.model.board.TsboardBasicConfig

// 게시판 기본 정보에 대한 JSON 응답 정의
@Serializable
data class BasicConfigDto(
    val id: String,
    val type: Int,
    val name: String
)

// 게시판 기본 정보를 엔티티로 변환하는 매퍼
fun BasicConfigDto.toEntity(): TsboardBasicConfig = TsboardBasicConfig(
    id = id,
    type = type,
    name = name
)
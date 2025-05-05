package me.data.remote.dto.common

import kotlinx.serialization.Serializable
import me.domain.model.common.TsboardTag

// 게시글의 해시태그에 대한 JSON 응답 정의
@Serializable
data class TagDto(
    val uid: Int,
    val name: String
)

// 해시태그에 응답에 대한 엔티티 변환 매퍼
fun TagDto.toEntity() = TsboardTag(
    uid = uid,
    name = name
)
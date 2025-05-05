package me.data.remote.dto.common

import kotlinx.serialization.Serializable
import me.domain.model.common.TsboardWriter

// 게시글 작성자 JSON 응답 정의
@Serializable
data class WriterDto(
    val uid: Int,
    val name: String,
    val profile: String,
    val signature: String
)

// 게시글 작성자 JSON 응답을 엔티티로 변환하는 매퍼
fun WriterDto.toEntity() = TsboardWriter(
    uid = uid,
    name = name,
    profile = profile,
    signature = signature
)
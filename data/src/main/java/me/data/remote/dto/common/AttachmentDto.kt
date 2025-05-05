package me.data.remote.dto.common

import kotlinx.serialization.Serializable
import me.domain.model.common.TsboardAttachment

// 일반 첨부 파일 JSON 응답
@Serializable
data class AttachmentDto(
    val uid: Int,
    val name: String,
    val size: Int
)

// 일반 첨부 파일 정보를 엔티티로 변환하는 매퍼
fun AttachmentDto.toEntity() = TsboardAttachment(
    uid = uid,
    name = name,
    size = size
)
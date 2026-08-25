package me.data.remote.dto.common

import kotlinx.serialization.Serializable
import me.domain.model.common.NuboImageFile

// 원본 저장 경로를 노출하지 않는 첨부 이미지 식별자
@Serializable
data class ImageFileDto(
    val uid: Int
)

// 첨부파일을 엔티티로 변환하는 매퍼
fun ImageFileDto.toEntity() = NuboImageFile(
    uid = uid
)

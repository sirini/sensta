package me.data.remote.dto.common

import kotlinx.serialization.Serializable
import me.domain.model.common.TsboardImageFile

// 파일 경로 및 고유번호에 대한 JSON 응답 정의
@Serializable
data class ImageFileDto(
    val uid: Int,
    val path: String
)

// 첨부파일을 엔티티로 변환하는 매퍼
fun ImageFileDto.toEntity(): TsboardImageFile = TsboardImageFile(
    uid = uid,
    path = path
)
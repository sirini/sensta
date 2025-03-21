package me.data.remote.dto.common

import kotlinx.serialization.Serializable
import me.domain.model.common.TsboardFile

// 파일 경로 및 고유번호에 대한 JSON 응답 정의
@Serializable
data class FileDto(
    val uid: Int,
    val path: String
)

// 첨부파일을 엔티티로 변환하는 매퍼
fun FileDto.toEntity(): TsboardFile = TsboardFile(
    uid = uid,
    path = path
)
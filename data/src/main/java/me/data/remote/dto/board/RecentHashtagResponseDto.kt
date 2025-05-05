package me.data.remote.dto.board

import kotlinx.serialization.Serializable
import me.domain.model.board.TsboardRecentHashtag
import me.domain.model.board.TsboardRecentHashtagResponse

// 최근에 작성된 해시태그들 가져오기에서의 JSON 응답 정의
@Serializable
data class RecentHashtagResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: List<RecentHashtagResult>
)

// 최근 작성된 해시태그들에 대한 결과 JSON
@Serializable
data class RecentHashtagResult(
    val uid: Int,
    val name: String,
    val postUid: Int
)

// 최근 해시태그의 결과를 엔티티로 매핑
fun RecentHashtagResponseDto.toEntity() = TsboardRecentHashtagResponse(
    success = success,
    error = error,
    code = code,
    result = result.map { it.toEntity() }
)

// 최근 해시태그의 결과 엔티티
fun RecentHashtagResult.toEntity() = TsboardRecentHashtag(
    uid = uid,
    name = name,
    postUid = postUid
)


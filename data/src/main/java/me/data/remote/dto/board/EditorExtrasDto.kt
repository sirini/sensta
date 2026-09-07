package me.data.remote.dto.board

import kotlinx.serialization.Serializable
import me.domain.model.board.NuboEditorCategory
import me.domain.model.board.NuboEditorConfig
import me.domain.model.board.NuboPublicUserSummary
import me.domain.model.board.NuboTagSuggestion

@Serializable
data class EditorConfigResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: EditorConfigResultDto? = null
)

@Serializable
data class EditorConfigResultDto(
    val config: EditorBoardConfigDto,
    val categories: List<EditorCategoryDto> = emptyList()
)

@Serializable
data class EditorBoardConfigDto(
    val uid: Int,
    val useCategory: Boolean
)

@Serializable
data class EditorCategoryDto(
    val uid: Int,
    val name: String
)

@Serializable
data class TagSuggestionResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: List<TagSuggestionDto> = emptyList()
)

@Serializable
data class TagSuggestionDto(
    val uid: Int,
    val name: String,
    val count: Int
)

@Serializable
data class PublicUserSummaryResponseDto(
    val success: Boolean,
    val error: String,
    val code: Int,
    val result: PublicUserSummaryDto? = null
)

@Serializable
data class PublicUserSummaryDto(
    val postCount: Long,
    val photoCount: Long,
    val likeCount: Long
)

fun EditorConfigResultDto.toEntity() = NuboEditorConfig(
    boardUid = config.uid,
    usesCategories = config.useCategory,
    categories = categories.map { NuboEditorCategory(it.uid, it.name) }
)

fun TagSuggestionDto.toEntity() = NuboTagSuggestion(uid, name, count)

fun PublicUserSummaryDto.toEntity() = NuboPublicUserSummary(postCount, photoCount, likeCount)

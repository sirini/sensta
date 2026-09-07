package me.domain.model.board

data class NuboEditorCategory(
    val uid: Int,
    val name: String
)

data class NuboEditorConfig(
    val boardUid: Int,
    val usesCategories: Boolean,
    val categories: List<NuboEditorCategory>
)

data class NuboTagSuggestion(
    val uid: Int,
    val name: String,
    val count: Int
)

data class NuboPublicUserSummary(
    val postCount: Long,
    val photoCount: Long,
    val likeCount: Long
)

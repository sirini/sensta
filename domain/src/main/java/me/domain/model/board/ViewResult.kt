package me.domain.model.board

// 게시글 보기 시 리턴 엔티티
data class ViewResult(
    val config: TsboardConfig,
    val post: TsboardPost,
    val images: List<TsboardImage>
)
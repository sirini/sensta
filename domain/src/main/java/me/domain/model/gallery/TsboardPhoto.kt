package me.domain.model.gallery

import me.domain.model.board.TsboardImage
import me.domain.model.board.TsboardWriter

// 갤러리 포토 엔티티
data class TsboardPhoto(
    val uid: Int,
    val like: Int,
    val liked: Boolean,
    val writer: TsboardWriter,
    val comment: Int,
    val title: String,
    val images: List<TsboardImage>
)
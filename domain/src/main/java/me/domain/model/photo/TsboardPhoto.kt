package me.domain.model.photo

import me.domain.model.common.TsboardWriter

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

// 비어있는 갤러리 포토 엔티티
val emptyPhoto = TsboardPhoto(
    uid = 0,
    like = 0,
    liked = false,
    comment = 0,
    images = emptyList(),
    title = "",
    writer = TsboardWriter(
        uid = 0,
        name = "",
        profile = "",
        signature = ""
    )
)
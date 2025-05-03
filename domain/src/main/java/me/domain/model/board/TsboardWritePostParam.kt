package me.domain.model.board

import android.content.Context
import android.net.Uri

// 사진 업로드용 파라미터 정의
data class TsboardWritePostParam(
    val context: Context,
    val boardUid: Int,
    val categoryUid: Int,
    val isNotice: Boolean,
    val isSecret: Boolean,
    val title: String,
    val content: String,
    val tags: List<String>,
    val attachments: List<Uri>,
    val token: String
)
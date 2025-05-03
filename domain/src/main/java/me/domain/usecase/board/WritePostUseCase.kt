package me.domain.usecase.board

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.flow
import me.domain.model.board.TsboardWritePostParam
import me.domain.repository.TsboardBoardRepository
import javax.inject.Inject

// 게시글 작성하기
class WritePostUseCase @Inject constructor(
    private val repository: TsboardBoardRepository
) {
    operator fun invoke(
        context: Context,
        boardUid: Int,
        categoryUid: Int,
        isNotice: Boolean = false,
        isSecret: Boolean = false,
        title: String,
        content: String,
        tags: List<String>,
        attachments: List<Uri>,
        token: String
    ) = flow {
        emit(
            repository.writePost(
                TsboardWritePostParam(
                    context = context,
                    boardUid = boardUid,
                    categoryUid = categoryUid,
                    isNotice = isNotice,
                    isSecret = isSecret,
                    title = title,
                    content = content,
                    tags = tags,
                    attachments = attachments,
                    token = token
                )
            )
        )
    }
}
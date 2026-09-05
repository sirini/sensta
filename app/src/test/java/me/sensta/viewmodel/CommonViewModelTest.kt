package me.sensta.viewmodel

import me.domain.model.board.NuboComment
import me.domain.model.common.NuboWriter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class CommonViewModelTest {
    @Test
    fun `답글 다이얼로그는 게시글과 대상 댓글을 함께 보관하고 닫을 때 비운다`() {
        val viewModel = CommonViewModel()
        val target = NuboComment(
            uid = 31,
            replyUid = 31,
            postUid = 10,
            writer = NuboWriter(7, "사진가", "", ""),
            like = 0,
            liked = false,
            submitted = LocalDateTime.of(2026, 9, 5, 12, 0),
            status = 0,
            content = "원래 댓글 내용입니다"
        )

        viewModel.openReplyCommentDialog(target)

        assertTrue(viewModel.showCommentDialog.value)
        assertEquals(10, viewModel.postUid.intValue)
        assertEquals(target, viewModel.commentReplyTarget.value)

        viewModel.closeWriteCommentDialog()

        assertFalse(viewModel.showCommentDialog.value)
        assertNull(viewModel.commentReplyTarget.value)
    }
}

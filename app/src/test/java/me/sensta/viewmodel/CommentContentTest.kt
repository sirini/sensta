package me.sensta.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Test

class CommentContentTest {
    @Test
    fun `댓글 수정 내용의 HTML 문자를 이스케이프한다`() {
        assertEquals(
            "&lt;script&gt;&quot;test&quot; &amp; &#39;x&#39;&lt;/script&gt;",
            "<script>\"test\" & 'x'</script>".toSafeCommentHtml()
        )
    }
}

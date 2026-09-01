package me.sensta.util

import org.junit.Assert.assertEquals
import org.junit.Test

class SharePostTest {
    @Test
    fun `게시물 공개 주소를 만든다`() {
        assertEquals(
            "https://sensta.me/board/photo/7525",
            postUrl(7525)
        )
    }

    @Test
    fun `공유 문구에는 정리한 제목과 공개 주소를 넣는다`() {
        assertEquals(
            "여름 사진\nhttps://sensta.me/board/photo/7373",
            postShareText(7373, "  여름 사진  ")
        )
    }
}

package me.sensta.util

import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class CustomTimeTest {
    @Test
    fun `댓글 작성 시각은 짧은 숫자 형식으로 표시한다`() {
        val submitted = LocalDateTime.of(2026, 9, 5, 12, 34)

        assertEquals("26/09/05 12:34", submitted.format(CustomTime.commentDate))
    }
}

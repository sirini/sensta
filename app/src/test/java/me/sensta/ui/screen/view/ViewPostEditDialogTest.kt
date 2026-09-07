package me.sensta.ui.screen.view

import org.junit.Assert.assertEquals
import org.junit.Test

class ViewPostEditDialogTest {
    @Test
    fun `태그 추천은 마지막 입력 조각을 교체한다`() {
        assertEquals("서울, 야경", applyTagSuggestion("서울, 야", "야경"))
        assertEquals("풍경", applyTagSuggestion("풍", "풍경"))
    }
}

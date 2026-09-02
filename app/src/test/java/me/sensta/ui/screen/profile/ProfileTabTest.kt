package me.sensta.ui.screen.profile

import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileTabTest {
    @Test
    fun profileTabsKeepTheUserFacingOrder() {
        assertEquals(
            listOf("작품", "정보", "업적"),
            ProfileTab.entries.map { it.label }
        )
    }
}

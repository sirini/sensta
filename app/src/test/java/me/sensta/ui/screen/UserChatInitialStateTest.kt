package me.sensta.ui.screen

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserChatInitialStateTest {
    @Test
    fun `message notification opens the message tab`() {
        assertTrue(shouldOpenMessageInitially(initialUserUid = 0, openMessageInitially = true))
    }

    @Test
    fun `push sender opens the message tab`() {
        assertTrue(shouldOpenMessageInitially(initialUserUid = 17, openMessageInitially = false))
    }

    @Test
    fun `regular user profile opens the photo tab`() {
        assertFalse(shouldOpenMessageInitially(initialUserUid = 0, openMessageInitially = false))
    }
}

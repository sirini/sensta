package me.sensta.ui.screen

import me.domain.model.user.NuboChatHistory
import me.sensta.viewmodel.latestIncomingMessageUid
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

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

    @Test
    fun `마지막 발신 메시지에만 읽음 상태를 표시한다`() {
        val messages = listOf(
            chat(uid = 10, userUid = 1, readAt = 1000),
            chat(uid = 11, userUid = 2),
            chat(uid = 12, userUid = 1)
        )

        assertEquals(12, latestOutgoingMessageUid(messages, currentUserUid = 1))
    }

    @Test
    fun `상대방이 보낸 가장 최신 메시지를 읽음 처리 대상으로 고른다`() {
        val messages = listOf(
            chat(uid = 10, userUid = 2),
            chat(uid = 11, userUid = 1),
            chat(uid = 12, userUid = 2)
        )

        assertEquals(12, latestIncomingMessageUid(messages, targetUserUid = 2))
    }

    private fun chat(uid: Int, userUid: Int, readAt: Long = 0) = NuboChatHistory(
        uid = uid,
        userUid = userUid,
        message = "메시지 $uid",
        timestamp = LocalDateTime.of(2026, 9, 6, 12, 0),
        readAt = readAt
    )
}

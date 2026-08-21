package me.sensta.push

import org.junit.Assert.assertEquals
import org.junit.Test

class PushDestinationResolverTest {
    @Test
    fun `대화 알림은 보낸 사용자와의 대화로 이동한다`() {
        val destination = PushDestinationResolver.resolve(
            PushEvent(notificationType = 4, postUid = 0, fromUserUid = 17)
        )

        assertEquals(PushDestination.Chat, destination)
    }

    @Test
    fun `사진 활동 알림은 해당 게시글로 이동한다`() {
        val destination = PushDestinationResolver.resolve(
            PushEvent(notificationType = 2, postUid = 7522, fromUserUid = 17)
        )

        assertEquals(PushDestination.Post, destination)
    }

    @Test
    fun `이동 정보가 부족한 알림은 알림 목록으로 이동한다`() {
        val destination = PushDestinationResolver.resolve(
            PushEvent(notificationType = null, postUid = 0, fromUserUid = 0)
        )

        assertEquals(PushDestination.Notification, destination)
    }
}

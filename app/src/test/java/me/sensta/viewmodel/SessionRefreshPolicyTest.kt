package me.sensta.viewmodel

import me.domain.model.auth.emptyUser
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class SessionRefreshPolicyTest {
    private val now = LocalDateTime.of(2026, 9, 5, 15, 0)

    @Test
    fun `완전한 세션은 한 시간 뒤 앱 복귀 시 갱신한다`() {
        val user = emptyUser.copy(
            uid = 7,
            name = "사진가",
            token = "access-token",
            refresh = "refresh-token",
            signin = now.minusHours(1)
        )

        assertTrue(user.needsSessionRefresh(now))
    }

    @Test
    fun `최근에 갱신했거나 비어 있는 세션은 다시 갱신하지 않는다`() {
        val recentUser = emptyUser.copy(
            uid = 7,
            name = "사진가",
            token = "access-token",
            refresh = "refresh-token",
            signin = now.minusMinutes(30)
        )

        assertFalse(recentUser.needsSessionRefresh(now))
        assertFalse(emptyUser.needsSessionRefresh(now))
    }
}

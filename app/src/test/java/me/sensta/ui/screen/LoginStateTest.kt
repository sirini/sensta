package me.sensta.ui.screen

import me.domain.model.auth.emptyUser
import me.sensta.viewmodel.state.LoginState
import org.junit.Assert.assertEquals
import org.junit.Test

class LoginStateTest {
    @Test
    fun `사용자 세션이 비었으면 로그인 완료 화면을 표시하지 않는다`() {
        assertEquals(
            LoginState.InputEmail,
            LoginState.LoginCompleted.visibleFor(emptyUser)
        )
    }

    @Test
    fun `완전한 사용자 세션에서만 로그인 완료 화면을 표시한다`() {
        val signedInUser = emptyUser.copy(
            uid = 7,
            name = "사진가",
            token = "access-token",
            refresh = "refresh-token"
        )

        assertEquals(
            LoginState.LoginCompleted,
            LoginState.LoginCompleted.visibleFor(signedInUser)
        )
    }
}

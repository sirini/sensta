package me.sensta.ui.screen

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SignupAvailabilityTest {
    @Test
    fun `일반 이메일과 초대 가입 정책은 사용할 수 있다`() {
        assertNull(signupAvailabilityMessage("verified_email", true, false, null))
        assertNull(signupAvailabilityMessage("invite_only", true, false, null))
    }

    @Test
    fun `중단된 가입과 메일 미설정은 안내한다`() {
        assertEquals(
            "현재 새 회원가입을 받고 있지 않습니다.",
            signupAvailabilityMessage("disabled", true, false, null)
        )
        assertEquals(
            "현재 이메일 인증 메일을 보낼 수 없습니다. Google 로그인을 이용해 주세요.",
            signupAvailabilityMessage("verified_email", false, false, null)
        )
    }
}

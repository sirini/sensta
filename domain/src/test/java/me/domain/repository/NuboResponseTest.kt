package me.domain.repository

import org.junit.Assert.assertSame
import org.junit.Assert.assertEquals
import org.junit.Test

class NuboResponseTest {
    @Test
    fun error_keepsDiagnosticCause() {
        val cause = IllegalStateException("잘못된 응답 타입")

        val response = NuboResponse.Error("사진 목록을 가져오지 못했습니다", cause)

        assertEquals("사진 목록을 가져오지 못했습니다", response.message)
        assertSame(cause, response.cause)
    }
}

package me.data.diagnostics

import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Test

class NuboClientIdentityInterceptorTest {
    @Test
    fun `adds stable app identity headers without credentials`() {
        val request = Request.Builder()
            .url("https://sensta.me/goapi/editor/write")
            .build()
            .withNuboClientIdentity("1.2.3")

        assertEquals("sensta-android", request.header("X-Nubo-Client"))
        assertEquals("1.2.3", request.header("X-Nubo-App-Version"))
        assertEquals(null, request.header("Authorization"))
    }
}

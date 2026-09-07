package me.data.diagnostics

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

class NuboUploadTimeoutInterceptorTest {
    @Test
    fun `extends read and write timeouts for post uploads`() {
        val observed = executeAndObserveTimeouts(
            Request.Builder()
                .url("https://sensta.me/goapi/editor/write")
                .post(ByteArray(0).toRequestBody())
                .build()
        )

        val expected = TimeUnit.SECONDS.toMillis(UPLOAD_TIMEOUT_SECONDS.toLong()).toInt()
        assertEquals(expected, observed.readMillis)
        assertEquals(expected, observed.writeMillis)
    }

    @Test
    fun `keeps default timeouts for ordinary requests`() {
        val observed = executeAndObserveTimeouts(
            Request.Builder()
                .url("https://sensta.me/goapi/board/list")
                .get()
                .build()
        )

        val expected = TimeUnit.SECONDS.toMillis(DEFAULT_TIMEOUT_SECONDS).toInt()
        assertEquals(expected, observed.readMillis)
        assertEquals(expected, observed.writeMillis)
    }

    private fun executeAndObserveTimeouts(request: Request): ObservedTimeouts {
        lateinit var observed: ObservedTimeouts
        val client = OkHttpClient.Builder()
            .readTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(NuboUploadTimeoutInterceptor())
            .addInterceptor { chain ->
                observed = ObservedTimeouts(
                    readMillis = chain.readTimeoutMillis(),
                    writeMillis = chain.writeTimeoutMillis()
                )
                Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body("{}".toResponseBody("application/json".toMediaType()))
                    .build()
            }
            .build()

        client.newCall(request).execute().close()
        return observed
    }

    private data class ObservedTimeouts(val readMillis: Int, val writeMillis: Int)

    private companion object {
        const val DEFAULT_TIMEOUT_SECONDS = 10L
    }
}

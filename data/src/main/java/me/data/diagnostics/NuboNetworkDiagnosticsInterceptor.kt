package me.data.diagnostics

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * 요청 본문과 인증값은 기록하지 않고 API 경로, 상태 코드, 소요 시간만 남긴다.
 */
class NuboNetworkDiagnosticsInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val operation = "${request.method} ${request.url.encodedPath}"
        val startedAt = System.nanoTime()

        Log.i(TAG, "요청 시작: $operation")
        return try {
            chain.proceed(request).also { response ->
                val elapsed = elapsedMilliseconds(startedAt)
                val level = if (response.isSuccessful) Log.INFO else Log.WARN
                Log.println(level, TAG, "요청 완료: $operation, HTTP ${response.code}, ${elapsed}ms")
            }
        } catch (error: IOException) {
            Log.e(TAG, "요청 실패: $operation, ${elapsedMilliseconds(startedAt)}ms", error)
            throw error
        } catch (error: RuntimeException) {
            Log.e(TAG, "응답 처리 실패: $operation, ${elapsedMilliseconds(startedAt)}ms", error)
            throw error
        }
    }

    private fun elapsedMilliseconds(startedAt: Long): Long =
        TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt)

    private companion object {
        const val TAG = "Sensta-Nubo"
    }
}

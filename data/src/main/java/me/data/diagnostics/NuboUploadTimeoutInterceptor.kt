package me.data.diagnostics

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 * 사진 전송과 서버의 썸네일·접근성 설명 후처리를 기다리는 글쓰기 요청에만 긴 제한을 적용한다.
 * 일반 API는 OkHttp 기본 제한을 유지해 연결 장애를 빠르게 알린다.
 */
class NuboUploadTimeoutInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!request.isPostUpload()) return chain.proceed(request)

        return chain
            .withWriteTimeout(UPLOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .withReadTimeout(UPLOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .proceed(request)
    }
}

internal const val UPLOAD_TIMEOUT_SECONDS = 120

internal fun Request.isPostUpload(): Boolean =
    method == "POST" && url.pathSegments.takeLast(2) == listOf("editor", "write")

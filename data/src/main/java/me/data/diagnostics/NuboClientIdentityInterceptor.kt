package me.data.diagnostics

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

internal fun Request.withNuboClientIdentity(appVersion: String): Request = newBuilder()
    .header("X-Nubo-Client", "sensta-android")
    .header("X-Nubo-App-Version", appVersion)
    .build()

/**
 * 서버가 게시글 작성 출처를 식별할 수 있도록 공개된 클라이언트 정보만 전달한다.
 * 이 값은 보안 자격 증명이 아니라 SENSTA 앱 활동 업적을 기록하기 위한 출처 표식이다.
 */
class NuboClientIdentityInterceptor(context: Context) : Interceptor {
    private val appVersion = runCatching {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()
    }.getOrDefault("")

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().withNuboClientIdentity(appVersion)
        return chain.proceed(request)
    }
}

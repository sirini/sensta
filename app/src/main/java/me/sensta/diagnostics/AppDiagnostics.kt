package me.sensta.diagnostics

import android.util.Log
import me.domain.repository.NuboResponse

/** 앱의 주요 실패 지점을 같은 태그와 형식으로 기록한다. */
object AppDiagnostics {
    private const val TAG = "Sensta-App"

    fun report(operation: String, error: NuboResponse.Error) {
        Log.e(TAG, "$operation 실패: ${error.message}", error.cause)
    }

    fun reportImage(path: String, error: Throwable?) {
        // 쿼리 문자열은 토큰이 포함될 수 있으므로 로그에서 제거한다.
        val safePath = path.substringBefore('?')
        Log.e(TAG, "이미지 로딩 실패: $safePath", error)
    }
}

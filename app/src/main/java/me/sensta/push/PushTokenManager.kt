package me.sensta.push

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.google.android.gms.tasks.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import me.domain.repository.TsboardResponse
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.home.RegisterPushDeviceUseCase
import me.domain.usecase.home.UnregisterPushDeviceUseCase
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class PushTokenManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val registerPushDeviceUseCase: RegisterPushDeviceUseCase,
    private val unregisterPushDeviceUseCase: UnregisterPushDeviceUseCase
) {
    // google-services.json이 없는 개발 환경에서는 기존 폴링 알림을 유지한다.
    fun isFirebaseConfigured(): Boolean = FirebaseApp.getApps(context).isNotEmpty()

    suspend fun synchronize(): Boolean {
        if (!isFirebaseConfigured()) return false
        val messaging = FirebaseMessaging.getInstance()
        if (runCatching { messaging.register().await() }.isFailure) return false
        val installationId = runCatching { FirebaseInstallations.getInstance().id.await() }
            .getOrNull() ?: return false
        return register(installationId)
    }

    suspend fun register(deviceToken: String): Boolean {
        val accessToken = getUserInfoUseCase().first().token
        if (accessToken.isBlank() || deviceToken.isBlank()) return false
        return registerPushDeviceUseCase(deviceToken, accessToken) is TsboardResponse.Success
    }

    suspend fun unregister(accessToken: String): Boolean {
        if (!isFirebaseConfigured() || accessToken.isBlank()) return false
        val installationId = runCatching { FirebaseInstallations.getInstance().id.await() }
            .getOrNull() ?: return false
        val unregistered =
            unregisterPushDeviceUseCase(installationId, accessToken) is TsboardResponse.Success
        if (unregistered) runCatching { FirebaseMessaging.getInstance().unregister().await() }
        return unregistered
    }
}

// Firebase의 Task API를 별도 코루틴 어댑터 의존성 없이 안전하게 기다린다.
private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        if (!continuation.isActive) return@addOnCompleteListener
        val exception = task.exception
        if (task.isSuccessful) {
            continuation.resume(task.result)
        } else if (exception != null) {
            continuation.resumeWithException(exception)
        } else {
            continuation.resumeWithException(IllegalStateException("Firebase 작업이 취소되었습니다"))
        }
    }
}

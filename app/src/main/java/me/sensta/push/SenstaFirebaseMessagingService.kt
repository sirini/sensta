package me.sensta.push

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.sensta.util.AppNotification
import javax.inject.Inject

@AndroidEntryPoint
// 최신 FCM은 등록 토큰 대신 onRegistered에서 전달하는 FID를 사용하므로 구형 린트 검사를 제외한다.
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class SenstaFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var pushTokenManager: PushTokenManager

    @Inject
    lateinit var pushEventBus: PushEventBus

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onRegistered(installationId: String) {
        super.onRegistered(installationId)
        serviceScope.launch { pushTokenManager.register(installationId) }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val event = PushEvent.from(message.data)
        pushEventBus.publish(event)
        AppNotification.showRemote(
            context = this,
            title = message.notification?.title ?: message.data["title"],
            body = message.notification?.body ?: message.data["body"],
            notificationId = message.messageId?.hashCode() ?: message.data.hashCode(),
            event = event
        )
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}

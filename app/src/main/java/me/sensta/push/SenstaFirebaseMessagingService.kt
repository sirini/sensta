package me.sensta.push

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
class SenstaFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var pushTokenManager: PushTokenManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onRegistered(installationId: String) {
        super.onRegistered(installationId)
        serviceScope.launch { pushTokenManager.register(installationId) }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        AppNotification.showRemote(
            context = this,
            title = message.notification?.title ?: message.data["title"],
            body = message.notification?.body ?: message.data["body"],
            notificationId = message.messageId?.hashCode() ?: message.data.hashCode()
        )
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}

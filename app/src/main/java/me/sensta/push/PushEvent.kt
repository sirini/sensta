package me.sensta.push

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

data class PushEvent(
    val notificationType: Int?,
    val postUid: Int,
    val fromUserUid: Int
) {
    companion object {
        fun from(data: Map<String, String>) = PushEvent(
            notificationType = data["type"]?.toIntOrNull(),
            postUid = data["postUid"]?.toIntOrNull() ?: 0,
            fromUserUid = data["fromUserUid"]?.toIntOrNull() ?: 0
        )
    }
}

enum class PushDestination {
    Notification,
    Post,
    Chat
}

object PushDestinationResolver {
    fun resolve(event: PushEvent): PushDestination = when {
        event.notificationType == CHAT_NOTIFICATION_TYPE && event.fromUserUid > 0 ->
            PushDestination.Chat
        event.postUid > 0 -> PushDestination.Post
        else -> PushDestination.Notification
    }

    private const val CHAT_NOTIFICATION_TYPE = 4
}

@Singleton
class PushEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<PushEvent>(extraBufferCapacity = 16)
    val events = _events.asSharedFlow()

    fun publish(event: PushEvent) {
        _events.tryEmit(event)
    }
}

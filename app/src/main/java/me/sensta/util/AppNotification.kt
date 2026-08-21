package me.sensta.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import me.domain.model.home.TsboardNotification
import me.domain.repository.TsboardResponse
import me.domain.repository.handle
import me.sensta.R
import me.sensta.push.PushEvent
import me.sensta.ui.MainActivity

object AppNotification {
    const val CHANNEL_ID = "activity"

    fun showRemote(
        context: Context,
        title: String?,
        body: String?,
        notificationId: Int,
        event: PushEvent
    ) {
        if (!hasPermission(context)) return

        val pendingIntent = notificationPendingIntent(context, notificationId, event)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title?.takeIf { it.isNotBlank() } ?: "Sensta 새 알림")
            .setContentText(body?.takeIf { it.isNotBlank() } ?: "새로운 활동이 있습니다")
            .setSmallIcon(R.drawable.notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notify(context, notificationId, notification)
    }

    // 새로운 알림이 있다면 앱 알림으로 업데이트하기
    suspend fun check(
        context: Context,
        noti: TsboardResponse<List<TsboardNotification>>
    ) {
        if (hasPermission(context)) {
            var uncheckedNotiUid = 0
            var uncheckedNotiCount = 0
            var uncheckedNotiText = ""

            val inboxStyle = NotificationCompat.InboxStyle()
            noti.handle { resp ->
                resp.forEach { noti ->
                    if (!noti.checked) {
                        val notiText = "${noti.fromUser.name}님이 ${
                            translate(noti.type)
                        }"
                        if (uncheckedNotiText.isEmpty()) {
                            uncheckedNotiText = notiText
                            uncheckedNotiUid = noti.uid
                        }
                        inboxStyle.addLine(notiText)
                        uncheckedNotiCount++
                    }
                }

                if (uncheckedNotiCount == 0) return@handle // 알림 없으면 아래 패스

                val pendingIntent = notificationPendingIntent(context, uncheckedNotiUid)

                val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Sensta 새 알림 ${uncheckedNotiCount}개")
                    .setContentText(uncheckedNotiText)
                    .setSmallIcon(R.drawable.notification)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.notification
                        )
                    )
                    .setStyle(inboxStyle)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

                notify(context, uncheckedNotiUid, notification)
            }
        }
    }

    private fun hasPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

    // 호출 전에 권한을 검사하지만 린트는 별도 함수의 검사 결과를 추적하지 못한다.
    @SuppressLint("MissingPermission")
    private fun notify(context: Context, notificationId: Int, notification: android.app.Notification) {
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    private fun notificationPendingIntent(
        context: Context,
        requestCode: Int,
        event: PushEvent? = null
    ): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("navigate_to", "notification")
            event?.notificationType?.let { putExtra("type", it.toString()) }
            putExtra("postUid", event?.postUid?.toString() ?: "0")
            putExtra("fromUserUid", event?.fromUserUid?.toString() ?: "0")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun translate(type: Int): String {
        return when (type) {
            0 -> "내 게시글을 좋아합니다"
            1 -> "내 댓글을 좋아합니다"
            2 -> "내 게시글에 댓글을 남겼습니다"
            3 -> "내 댓글에 답글을 남겼습니다"
            else -> "나에게 쪽지를 보냈습니다"
        }
    }
}

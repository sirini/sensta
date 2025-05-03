package me.sensta.util

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import me.domain.model.home.TsboardNotification
import me.domain.repository.TsboardResponse
import me.domain.repository.handle
import me.sensta.R
import me.sensta.ui.MainActivity

object AppNotification {

    // 새로운 알림이 있다면 앱 알림으로 업데이트하기
    suspend fun check(
        context: Context,
        noti: TsboardResponse<List<TsboardNotification>>
    ) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
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

                val intent = Intent(context, MainActivity::class.java).apply {
                    putExtra("navigate_to", "notification")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent =
                    PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE
                    )

                val notification = NotificationCompat.Builder(context, "default")
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

                NotificationManagerCompat.from(context)
                    .notify(uncheckedNotiUid, notification)
            }
        }
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
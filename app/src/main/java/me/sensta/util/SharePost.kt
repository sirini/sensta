package me.sensta.util

import android.content.Context
import android.content.Intent
import me.data.env.Env

fun postUrl(postUid: Int): String =
    "${Env.DOMAIN}/board/${Env.BOARD_ID}/$postUid"

fun postShareText(postUid: Int, title: String): String =
    "${title.trim()}\n${postUrl(postUid)}"

fun sharePost(context: Context, postUid: Int, title: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title.trim())
        putExtra(Intent.EXTRA_TEXT, postShareText(postUid, title))
    }
    context.startActivity(Intent.createChooser(sendIntent, "SENSTA 사진 공유"))
}

package me.data.util

import java.security.MessageDigest

// 문자열을 SHA256 알고리즘으로 해시한 값으로 변환
fun String.toSHA256(): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
}
package me.sensta.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object CustomTime {
    val fullDate = DateTimeFormatter.ofPattern("yy년 MM월 dd일 HH시 mm분")
    val simpleDate = DateTimeFormatter.ofPattern("yy년 MM월 dd일")
}

fun CustomTime.now(): LocalDateTime {
    return ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Asia/Seoul")).toLocalDateTime()
}
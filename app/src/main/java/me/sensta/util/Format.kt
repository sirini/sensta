package me.sensta.util

import java.time.format.DateTimeFormatter

object Format {
    val fullDate = DateTimeFormatter.ofPattern("yy년 MM월 dd일 HH시 mm분")
    val simpleDate = DateTimeFormatter.ofPattern("yy년 MM월 dd일")
}
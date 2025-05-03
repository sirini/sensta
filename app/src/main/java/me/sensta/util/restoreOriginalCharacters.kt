package me.sensta.util

fun convertHtmlToText(text: String): String {
    var t = text.replace("&amp;", "&")
    t = t.replace("&amp;", "&")
    t = t.replace("&#x27;", "'")
    t = t.replace("&quot;", "\"")
    t = t.replace("&lt;", "<")
    t = t.replace("&gt;", ">")
    return t
}
package me.sensta.util

private val thumbnailPathPattern =
    Regex("""(/upload/thumbnails/(?:[^/]+/)*)t([^/?]+)(?=$|[?#])""")

/** 목록용 t*.webp 경로를 GOAPI가 함께 생성한 f*.webp 미리보기 경로로 바꾼다. */
fun String.toPreviewImagePath(): String =
    thumbnailPathPattern.replace(this, "\$1f\$2")

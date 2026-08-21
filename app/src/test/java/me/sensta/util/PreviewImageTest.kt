package me.sensta.util

import org.junit.Assert.assertEquals
import org.junit.Test

class PreviewImageTest {
    @Test
    fun `목록 썸네일을 전체 미리보기 경로로 바꾼다`() {
        assertEquals(
            "/upload/thumbnails/2026/08/fphoto.webp?v=2",
            "/upload/thumbnails/2026/08/tphoto.webp?v=2".toPreviewImagePath()
        )
    }

    @Test
    fun `썸네일 규칙과 다른 경로는 그대로 둔다`() {
        assertEquals(
            "/upload/original/photo.webp",
            "/upload/original/photo.webp".toPreviewImagePath()
        )
    }
}

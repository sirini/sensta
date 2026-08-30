package me.sensta.editor

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class PhotoFilterMatrixTest {
    @Test
    fun `강도가 0이면 모든 필터가 원본 색상 행렬을 반환한다`() {
        val original = PhotoFilterMatrix.values(PhotoFilter.ORIGINAL, 1f)

        PhotoFilter.entries.forEach { filter ->
            assertArrayEquals(original, PhotoFilterMatrix.values(filter, 0f), 0.0001f)
        }
    }

    @Test
    fun `강도가 커지면 필터 색상 행렬이 달라진다`() {
        val original = PhotoFilterMatrix.values(PhotoFilter.ORIGINAL, 1f)

        PhotoFilter.entries.filterNot { it == PhotoFilter.ORIGINAL }.forEach { filter ->
            assertFalse(original.contentEquals(PhotoFilterMatrix.values(filter, 1f)))
        }
    }

    @Test
    fun `필터 강도는 0에서 1 사이로 제한된다`() {
        val below = PhotoFilterMatrix.values(PhotoFilter.WARM, -1f)
        val zero = PhotoFilterMatrix.values(PhotoFilter.WARM, 0f)
        val above = PhotoFilterMatrix.values(PhotoFilter.WARM, 2f)
        val one = PhotoFilterMatrix.values(PhotoFilter.WARM, 1f)

        assertArrayEquals(zero, below, 0.0001f)
        assertArrayEquals(one, above, 0.0001f)
    }
}

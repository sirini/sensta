package me.sensta.editor

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class PhotoRendererInstrumentedTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var source: File

    @Before
    fun setUp() {
        source = File(context.cacheDir, "sensta-renderer-source.jpg")
        createSource(source, includeLocation = false)
    }

    @After
    fun tearDown() {
        source.delete()
        PhotoEditCache.clear(context)
    }

    @Test
    fun noEditsReturnsOriginalWithoutChangingBytes() {
        val original = source.readBytes()
        val sourceUri = Uri.fromFile(source)

        val result = PhotoRenderer.render(context, EditablePhoto(sourceUri))

        assertEquals(sourceUri, result)
        assertArrayEquals(original, source.readBytes())
    }

    @Test
    fun noEditsWithLocationRenderPrivacySafeCopy() {
        createSource(source, includeLocation = true)
        val original = source.readBytes()
        val sourceUri = Uri.fromFile(source)

        val result = PhotoRenderer.render(context, EditablePhoto(sourceUri))
        val output = File(requireNotNull(result.path))
        val outputExif = ExifInterface(output.absolutePath)

        assertNotEquals(sourceUri, result)
        assertNull(outputExif.latLong)
        assertEquals("SENSTA QA", outputExif.getAttribute(ExifInterface.TAG_MAKE))
        assertEquals(1200, outputExif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0))
        assertEquals(1600, outputExif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0))
        assertEquals(
            ExifInterface(source.absolutePath).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            ),
            outputExif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
        )
        assertArrayEquals(original, source.readBytes())
        assertTrue(requireNotNull(ExifInterface(source.absolutePath).latLong).isNotEmpty())
    }

    @Test
    fun editsRenderJpegPreserveSafeExifAndStripLocation() {
        createSource(source, includeLocation = true)
        val original = source.readBytes()
        val photo = EditablePhoto(
            originalUri = Uri.fromFile(source),
            rotation = 90,
            mirrored = true,
            filter = PhotoFilter.WARM,
            filterIntensity = 0.5f
        )

        val result = PhotoRenderer.render(context, photo)
        val output = File(requireNotNull(result.path))
        val exif = ExifInterface(output.absolutePath)

        assertTrue(output.readBytes().take(2) == listOf(0xFF.toByte(), 0xD8.toByte()))
        assertEquals(1600, exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0))
        assertEquals(1200, exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0))
        assertEquals(
            ExifInterface.ORIENTATION_NORMAL,
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        )
        assertEquals("SENSTA QA", exif.getAttribute(ExifInterface.TAG_MAKE))
        assertEquals("Virtual Camera", exif.getAttribute(ExifInterface.TAG_MODEL))
        assertEquals("2026:08:27 00:00:00", exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL))
        assertEquals("200", exif.getAttribute(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY))
        assertEquals(2.8, exif.getAttributeDouble(ExifInterface.TAG_F_NUMBER, 0.0), 0.001)
        assertEquals(1.0 / 125.0, exif.getAttributeDouble(ExifInterface.TAG_EXPOSURE_TIME, 0.0), 0.0001)
        assertEquals(50.0, exif.getAttributeDouble(ExifInterface.TAG_FOCAL_LENGTH, 0.0), 0.001)
        assertNull(exif.latLong)
        assertNotEquals(original.size, output.length().toInt())
        assertArrayEquals(original, source.readBytes())
    }

    private fun createSource(file: File, includeLocation: Boolean) {
        val bitmap = Bitmap.createBitmap(1200, 1600, Bitmap.Config.ARGB_8888)
        Canvas(bitmap).apply {
            drawColor(Color.rgb(244, 238, 225))
            drawRect(0f, 0f, 600f, 800f, Paint().apply { color = Color.RED })
            drawRect(600f, 0f, 1200f, 800f, Paint().apply { color = Color.BLUE })
            drawRect(0f, 800f, 600f, 1600f, Paint().apply { color = Color.GREEN })
            drawRect(600f, 800f, 1200f, 1600f, Paint().apply { color = Color.YELLOW })
        }
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it) }
        bitmap.recycle()

        ExifInterface(file.absolutePath).apply {
            setAttribute(ExifInterface.TAG_MAKE, "SENSTA QA")
            setAttribute(ExifInterface.TAG_MODEL, "Virtual Camera")
            setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, "2026:08:27 00:00:00")
            setAttribute(ExifInterface.TAG_F_NUMBER, "28/10")
            setAttribute(ExifInterface.TAG_EXPOSURE_TIME, "1/125")
            setAttribute(ExifInterface.TAG_FOCAL_LENGTH, "50/1")
            setAttribute(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY, "200")
            if (includeLocation) setLatLong(37.401, 127.108)
            saveAttributes()
        }
    }
}

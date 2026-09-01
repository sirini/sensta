package me.sensta.editor

import android.content.Context
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.File

object ExifMetadata {
    fun hasSensitiveLocation(context: Context, sourceUri: Uri): Boolean =
        runCatching {
            context.contentResolver.openInputStream(sourceUri)?.use(::ExifInterface)
        }.getOrNull()?.let { source ->
            sensitiveLocationTags.any(source::hasAttribute)
        } == true

    fun copy(context: Context, sourceUri: Uri, target: File, width: Int, height: Int) {
        val source = runCatching {
            context.contentResolver.openInputStream(sourceUri)?.use(::ExifInterface)
        }.getOrNull()
        val destination = runCatching { ExifInterface(target.absolutePath) }.getOrNull() ?: return
        if (source != null) {
            copiedTags.forEach { tag ->
                source.getAttribute(tag)?.let { destination.setAttribute(tag, it) }
            }
        }
        clearSensitiveLocation(destination)
        destination.setAttribute(ExifInterface.TAG_IMAGE_WIDTH, width.toString())
        destination.setAttribute(ExifInterface.TAG_IMAGE_LENGTH, height.toString())
        destination.setAttribute(ExifInterface.TAG_PIXEL_X_DIMENSION, width.toString())
        destination.setAttribute(ExifInterface.TAG_PIXEL_Y_DIMENSION, height.toString())
        destination.setAttribute(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL.toString()
        )
        destination.saveAttributes()
    }

    fun removeSensitiveLocation(target: File) {
        val destination = ExifInterface(target.absolutePath)
        clearSensitiveLocation(destination)
        destination.saveAttributes()
    }

    private fun clearSensitiveLocation(destination: ExifInterface) {
        sensitiveLocationTags.forEach { tag -> destination.setAttribute(tag, null) }
    }

    private val copiedTags = listOf(
        ExifInterface.TAG_MAKE,
        ExifInterface.TAG_MODEL,
        ExifInterface.TAG_LENS_MAKE,
        ExifInterface.TAG_LENS_MODEL,
        ExifInterface.TAG_DATETIME,
        ExifInterface.TAG_DATETIME_ORIGINAL,
        ExifInterface.TAG_DATETIME_DIGITIZED,
        ExifInterface.TAG_OFFSET_TIME,
        ExifInterface.TAG_OFFSET_TIME_ORIGINAL,
        ExifInterface.TAG_OFFSET_TIME_DIGITIZED,
        ExifInterface.TAG_SUBSEC_TIME,
        ExifInterface.TAG_SUBSEC_TIME_ORIGINAL,
        ExifInterface.TAG_SUBSEC_TIME_DIGITIZED,
        ExifInterface.TAG_F_NUMBER,
        ExifInterface.TAG_APERTURE_VALUE,
        ExifInterface.TAG_EXPOSURE_TIME,
        ExifInterface.TAG_EXPOSURE_BIAS_VALUE,
        ExifInterface.TAG_FOCAL_LENGTH,
        ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY,
        ExifInterface.TAG_FLASH,
        ExifInterface.TAG_WHITE_BALANCE,
        ExifInterface.TAG_COLOR_SPACE,
        ExifInterface.TAG_SOFTWARE,
        ExifInterface.TAG_ARTIST,
        ExifInterface.TAG_COPYRIGHT,
        ExifInterface.TAG_IMAGE_DESCRIPTION,
        ExifInterface.TAG_USER_COMMENT
    )

    // 공개 업로드에는 정확한 촬영 위치와 그에 딸린 GPS 메타데이터를 승계하지 않는다.
    private val sensitiveLocationTags = listOf(
        ExifInterface.TAG_GPS_ALTITUDE,
        ExifInterface.TAG_GPS_ALTITUDE_REF,
        ExifInterface.TAG_GPS_DATESTAMP,
        ExifInterface.TAG_GPS_LATITUDE,
        ExifInterface.TAG_GPS_LATITUDE_REF,
        ExifInterface.TAG_GPS_LONGITUDE,
        ExifInterface.TAG_GPS_LONGITUDE_REF,
        ExifInterface.TAG_GPS_PROCESSING_METHOD,
        ExifInterface.TAG_GPS_TIMESTAMP
    )
}

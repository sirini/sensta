package me.sensta.editor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.File

object PhotoRenderer {
    private const val MAX_BITMAP_SIDE = 3072
    private const val JPEG_QUALITY = 95

    fun render(context: Context, photo: EditablePhoto): Uri {
        val mustStripLocation = ExifMetadata.hasSensitiveLocation(context, photo.originalUri)
        if (!photo.needsRendering && photo.croppedUri == null && !mustStripLocation) {
            return photo.originalUri
        }
        if (!photo.needsRendering && photo.croppedUri == null &&
            mustStripLocation && isJpeg(context, photo.originalUri)
        ) {
            return createPrivacySafeJpegCopy(context, photo.originalUri)
        }
        if (!photo.needsRendering && photo.croppedUri != null &&
            isJpeg(context, photo.croppedUri)
        ) {
            preserveCropMetadata(context, photo)
            return photo.croppedUri
        }

        var working = decodeSampled(context, photo.previewUri)
        val outputUri = PhotoEditCache.createUri(context, "render")
        val output = requireNotNull(outputUri.path).let(::File)

        try {
            working = replace(working, applyExifOrientation(context, photo.previewUri, working))
            working = replace(working, transform(working, photo.rotation, photo.mirrored))
            working = replace(working, applyFilter(working, photo.filter, photo.filterIntensity))
            output.outputStream().buffered().use { stream ->
                check(working.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream))
            }
            ExifMetadata.copy(context, photo.originalUri, output, working.width, working.height)
        } catch (error: Exception) {
            output.delete()
            throw error
        } finally {
            working.recycle()
        }
        return outputUri
    }

    private fun createPrivacySafeJpegCopy(context: Context, sourceUri: Uri): Uri {
        val outputUri = PhotoEditCache.createUri(context, "privacy")
        val output = requireNotNull(outputUri.path).let(::File)
        try {
            val input = requireNotNull(context.contentResolver.openInputStream(sourceUri)) {
                "사진을 불러오지 못했습니다"
            }
            input.use { source ->
                output.outputStream().buffered().use { target -> source.copyTo(target) }
            }
            ExifMetadata.removeSensitiveLocation(output)
        } catch (error: Exception) {
            output.delete()
            throw error
        }
        return outputUri
    }

    private fun preserveCropMetadata(context: Context, photo: EditablePhoto) {
        val cropUri = photo.croppedUri ?: return
        val path = cropUri.path ?: return
        val bounds = readBounds(context, cropUri)
        ExifMetadata.copy(context, photo.originalUri, File(path), bounds.first, bounds.second)
    }

    private fun decodeSampled(context: Context, uri: Uri): Bitmap {
        val (width, height) = readBounds(context, uri)
        var sampleSize = 1
        while (width / sampleSize > MAX_BITMAP_SIDE || height / sampleSize > MAX_BITMAP_SIDE) {
            sampleSize *= 2
        }
        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        return context.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, options)
        } ?: error("사진을 불러오지 못했습니다")
    }

    private fun readBounds(context: Context, uri: Uri): Pair<Int, Int> {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, options)
        }
        check(options.outWidth > 0 && options.outHeight > 0) { "사진 크기를 확인하지 못했습니다" }
        return options.outWidth to options.outHeight
    }

    private fun isJpeg(context: Context, uri: Uri): Boolean =
        context.contentResolver.openInputStream(uri)?.use { input ->
            input.read() == 0xFF && input.read() == 0xD8
        } == true

    private fun applyExifOrientation(context: Context, uri: Uri, source: Bitmap): Bitmap {
        val orientation = runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                ExifInterface(input).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            }
        }.getOrNull() ?: ExifInterface.ORIENTATION_NORMAL
        val (rotation, mirrored) = when (orientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> 0 to true
            ExifInterface.ORIENTATION_ROTATE_180 -> 180 to false
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> 180 to true
            ExifInterface.ORIENTATION_TRANSPOSE -> 90 to true
            ExifInterface.ORIENTATION_ROTATE_90 -> 90 to false
            ExifInterface.ORIENTATION_TRANSVERSE -> 270 to true
            ExifInterface.ORIENTATION_ROTATE_270 -> 270 to false
            else -> 0 to false
        }
        return transform(source, rotation, mirrored)
    }

    private fun transform(source: Bitmap, rotation: Int, mirrored: Boolean): Bitmap {
        if (rotation == 0 && !mirrored) return source
        val matrix = Matrix().apply {
            if (mirrored) postScale(-1f, 1f)
            if (rotation != 0) postRotate(rotation.toFloat())
        }
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun applyFilter(source: Bitmap, filter: PhotoFilter, intensity: Float): Bitmap {
        if (filter == PhotoFilter.ORIGINAL) return source
        val output = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            colorFilter = ColorMatrixColorFilter(ColorMatrix(PhotoFilterMatrix.values(filter, intensity)))
        }
        Canvas(output).apply {
            drawColor(Color.WHITE)
            drawBitmap(source, 0f, 0f, paint)
        }
        return output
    }

    private fun replace(previous: Bitmap, next: Bitmap): Bitmap {
        if (previous !== next) previous.recycle()
        return next
    }
}

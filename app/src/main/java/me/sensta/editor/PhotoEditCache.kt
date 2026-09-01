package me.sensta.editor

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

object PhotoEditCache {
    private const val DIRECTORY = "sensta-photo-editor"

    fun createUri(context: Context, prefix: String): Uri {
        val directory = File(context.cacheDir, DIRECTORY).apply { mkdirs() }
        return Uri.fromFile(File(directory, "$prefix-${UUID.randomUUID()}.jpg"))
    }

    fun createCropUri(context: Context, photoIndex: Int): Uri =
        createUri(context, "crop-$photoIndex")

    fun cropPhotoIndex(uri: Uri): Int? = uri.lastPathSegment
        ?.substringAfter("crop-", missingDelimiterValue = "")
        ?.substringBefore('-')
        ?.toIntOrNull()

    fun delete(uri: Uri?) {
        if (uri?.scheme == "file") uri.path?.let(::File)?.delete()
    }

    fun clear(context: Context) {
        File(context.cacheDir, DIRECTORY).listFiles()?.forEach(File::delete)
    }
}

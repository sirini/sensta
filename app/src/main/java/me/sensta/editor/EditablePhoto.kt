package me.sensta.editor

import android.net.Uri

data class EditablePhoto(
    val originalUri: Uri,
    val croppedUri: Uri? = null,
    val renderedUri: Uri? = null,
    val rotation: Int = 0,
    val mirrored: Boolean = false,
    val filter: PhotoFilter = PhotoFilter.ORIGINAL,
    val filterIntensity: Float = 1f
) {
    val previewUri: Uri get() = croppedUri ?: originalUri
    val uploadUri: Uri get() = renderedUri ?: croppedUri ?: originalUri

    val needsRendering: Boolean
        get() = rotation != 0 || mirrored ||
            (filter != PhotoFilter.ORIGINAL && filterIntensity > 0f)
}

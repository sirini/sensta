package me.sensta.editor

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf

class PhotoEditorState {
    private val _photos = mutableStateOf<List<EditablePhoto>>(emptyList())
    val photos: State<List<EditablePhoto>> get() = _photos

    private val _selectedPhotoIndex = mutableIntStateOf(0)
    val selectedPhotoIndex: State<Int> get() = _selectedPhotoIndex

    val uploadUris: List<Uri> get() = _photos.value.map(EditablePhoto::uploadUri)

    fun setPhotos(uris: List<Uri>) {
        _photos.value = uris.map(::EditablePhoto)
        _selectedPhotoIndex.intValue = 0
    }

    fun setPrepared(photos: List<EditablePhoto>) {
        _photos.value = photos
    }

    fun clear(context: Context) {
        PhotoEditCache.clear(context)
        _photos.value = emptyList()
        _selectedPhotoIndex.intValue = 0
    }

    fun clearFiles(context: Context) = PhotoEditCache.clear(context)

    fun select(index: Int) {
        if (index in _photos.value.indices) _selectedPhotoIndex.intValue = index
    }

    fun applyCrop(index: Int, croppedUri: Uri) {
        if (index !in _photos.value.indices) return
        val previous = _photos.value[index]
        deleteRendered(previous)
        PhotoEditCache.delete(previous.croppedUri)
        update(index, previous.copy(croppedUri = croppedUri, renderedUri = null))
    }

    fun rotate() = editSelected { photo ->
        photo.copy(rotation = (photo.rotation + 90) % 360)
    }

    fun mirror() = editSelected { photo ->
        photo.copy(mirrored = !photo.mirrored)
    }

    fun setFilter(filter: PhotoFilter) = editSelected { photo ->
        photo.copy(filter = filter)
    }

    fun setIntensity(intensity: Float) = editSelected { photo ->
        photo.copy(filterIntensity = intensity.coerceIn(0f, 1f))
    }

    fun reset() {
        val index = _selectedPhotoIndex.intValue
        val current = _photos.value.getOrNull(index) ?: return
        deleteRendered(current)
        PhotoEditCache.delete(current.croppedUri)
        update(index, EditablePhoto(current.originalUri))
    }

    private fun editSelected(change: (EditablePhoto) -> EditablePhoto) {
        val index = _selectedPhotoIndex.intValue
        val current = _photos.value.getOrNull(index) ?: return
        deleteRendered(current)
        update(index, change(current).copy(renderedUri = null))
    }

    private fun update(index: Int, photo: EditablePhoto) {
        _photos.value = _photos.value.toMutableList().apply { this[index] = photo }
    }

    private fun deleteRendered(photo: EditablePhoto) {
        if (photo.renderedUri != photo.croppedUri && photo.renderedUri != photo.originalUri) {
            PhotoEditCache.delete(photo.renderedUri)
        }
    }
}

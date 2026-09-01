package me.data.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

object Upload {
    data class PreparedFile(
        val part: MultipartBody.Part,
        val temporaryFile: File
    ) {
        fun cleanUp() {
            temporaryFile.delete()
        }
    }

    // 콘텐츠 URI를 앱 전용 임시 파일로 안전하게 복사해 업로드 파트로 변환한다.
    fun prepareImage(context: Context, uri: Uri, name: String): PreparedFile? {
        val resolver = context.contentResolver
        val displayName = getDisplayName(context, uri)
        val suffix = displayName
            ?.substringAfterLast('.', missingDelimiterValue = "")
            ?.takeIf { it.matches(Regex("[A-Za-z0-9]{1,10}")) }
            ?.let { ".$it" }
            ?: ".jpg"
        val temporaryFile = File.createTempFile("sensta-upload-", suffix, context.cacheDir)

        return try {
            val inputStream = resolver.openInputStream(uri)
            if (inputStream == null) {
                temporaryFile.delete()
                return null
            }
            inputStream.use { input ->
                temporaryFile.outputStream().use { output -> input.copyTo(output) }
            }

            val mediaType = resolver.getType(uri)
                ?.toMediaTypeOrNull()
                ?: if (suffix.equals(".jpg", ignoreCase = true) ||
                    suffix.equals(".jpeg", ignoreCase = true)
                ) {
                    "image/jpeg".toMediaTypeOrNull()
                } else {
                    "image/*".toMediaTypeOrNull()
                }
            val requestBody = temporaryFile.asRequestBody(mediaType)
            val uploadName = displayName ?: temporaryFile.name
            PreparedFile(
                part = MultipartBody.Part.createFormData(name, uploadName, requestBody),
                temporaryFile = temporaryFile
            )
        } catch (_: Exception) {
            temporaryFile.delete()
            null
        }
    }

    private fun getDisplayName(context: Context, uri: Uri): String? =
        if (uri.scheme == "file") {
            uri.path?.let(::File)?.name
        } else {
            context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0 && cursor.moveToFirst()) cursor.getString(nameIndex) else null
            }
        }
}

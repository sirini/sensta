package me.data.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

object Upload {
    // 선택한 파일을 업로드 가능한 형태로 변홚하여 반환
    fun uriToMultipart(context: Context, uri: Uri, name: String): MultipartBody.Part? {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val fileName = getFileNameFromUri(context, uri) ?: "${System.currentTimeMillis()}.jpg"
        val file = File(context.cacheDir, fileName)
        file.outputStream().use { output -> inputStream.copyTo(output) }

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(name, file.name, requestFile)
    }

    // Uri에서 파일 이름 가져오기
    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null) ?: return null
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()

        val fileName = returnCursor.getString(nameIndex)
        returnCursor.close()

        return fileName
    }
}
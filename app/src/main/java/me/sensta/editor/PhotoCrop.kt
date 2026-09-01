package me.sensta.editor

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio

object PhotoCrop {
    fun createIntent(context: Context, source: Uri, destination: Uri): Intent {
        val options = UCrop.Options().apply {
            setCompressionFormat(Bitmap.CompressFormat.JPEG)
            setCompressionQuality(95)
            setFreeStyleCropEnabled(false)
            setToolbarTitle("사진 자르기")
            setAspectRatioOptions(
                0,
                AspectRatio("원본", 0f, 0f),
                AspectRatio("4:5", 4f, 5f),
                AspectRatio("3:4", 3f, 4f)
            )
            withMaxResultSize(4096, 4096)
        }
        return UCrop.of(source, destination).withOptions(options).getIntent(context)
    }
}

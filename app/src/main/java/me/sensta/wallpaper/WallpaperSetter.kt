package me.sensta.wallpaper

import android.app.WallpaperManager
import android.content.Context
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.sensta.diagnostics.AppDiagnostics

enum class WallpaperSetResult {
    SUCCESS,
    NOT_SUPPORTED,
    NOT_ALLOWED,
    IMAGE_LOAD_FAILED,
    FAILED
}

enum class WallpaperTarget(
    val label: String,
    internal val flags: Int
) {
    HOME_SCREEN("홈 화면", WallpaperManager.FLAG_SYSTEM),
    LOCK_SCREEN("잠금 화면", WallpaperManager.FLAG_LOCK),
    BOTH(
        "홈 및 잠금 화면",
        WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
    )
}

object WallpaperSetter {
    suspend fun set(
        context: Context,
        imageUrl: String,
        target: WallpaperTarget
    ): WallpaperSetResult =
        withContext(Dispatchers.IO) {
            val appContext = context.applicationContext
            val wallpaperManager = WallpaperManager.getInstance(appContext)

            if (!wallpaperManager.isWallpaperSupported) {
                return@withContext WallpaperSetResult.NOT_SUPPORTED
            }
            if (!wallpaperManager.isSetWallpaperAllowed) {
                return@withContext WallpaperSetResult.NOT_ALLOWED
            }

            val drawable = try {
                val request = ImageRequest.Builder(appContext)
                    .data(imageUrl)
                    .allowHardware(false)
                    .build()
                (appContext.imageLoader.execute(request) as? SuccessResult)?.drawable
            } catch (error: Exception) {
                AppDiagnostics.reportImage(imageUrl, error)
                null
            } ?: return@withContext WallpaperSetResult.IMAGE_LOAD_FAILED

            try {
                wallpaperManager.setBitmap(
                    drawable.toBitmap(),
                    null,
                    true,
                    target.flags
                )
                WallpaperSetResult.SUCCESS
            } catch (error: SecurityException) {
                AppDiagnostics.report("${target.label} 배경화면 설정", error.message ?: "권한이 없습니다")
                WallpaperSetResult.NOT_ALLOWED
            } catch (error: Exception) {
                AppDiagnostics.report("${target.label} 배경화면 설정", error.message ?: "알 수 없는 오류")
                WallpaperSetResult.FAILED
            }
        }
}

package me.sensta.wallpaper

import android.app.WallpaperManager
import org.junit.Assert.assertEquals
import org.junit.Test

class WallpaperTargetTest {
    @Test
    fun `각 배경화면 대상이 올바른 시스템 플래그를 사용한다`() {
        assertEquals(WallpaperManager.FLAG_SYSTEM, WallpaperTarget.HOME_SCREEN.flags)
        assertEquals(WallpaperManager.FLAG_LOCK, WallpaperTarget.LOCK_SCREEN.flags)
        assertEquals(
            WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK,
            WallpaperTarget.BOTH.flags
        )
    }
}

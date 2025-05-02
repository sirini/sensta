package me.sensta.viewmodel.local

import androidx.compose.runtime.staticCompositionLocalOf
import me.sensta.viewmodel.UploadViewModel

val LocalUploadViewModel = staticCompositionLocalOf<UploadViewModel> {
    error("No UploadViewModel provided")
}
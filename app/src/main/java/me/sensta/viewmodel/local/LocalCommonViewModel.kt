package me.sensta.viewmodel.local

import androidx.compose.runtime.staticCompositionLocalOf
import me.sensta.viewmodel.CommonViewModel

val LocalCommonViewModel = staticCompositionLocalOf<CommonViewModel> {
    error("No CommonViewModel provided")
}
package me.sensta.viewmodel.common

import androidx.compose.runtime.staticCompositionLocalOf

val LocalCommonViewModel = staticCompositionLocalOf<CommonViewModel> {
    error("No CommonViewModel provided")
}
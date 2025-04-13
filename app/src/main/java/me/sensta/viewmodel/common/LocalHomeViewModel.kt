package me.sensta.viewmodel.common

import androidx.compose.runtime.staticCompositionLocalOf
import me.sensta.viewmodel.HomeViewModel

val LocalHomeViewModel = staticCompositionLocalOf<HomeViewModel> {
    error("No HomeViewModel provided")
}
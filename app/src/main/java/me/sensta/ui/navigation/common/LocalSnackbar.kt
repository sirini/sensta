package me.sensta.ui.navigation.common

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSnackbar = staticCompositionLocalOf<SnackbarHostState> {
    error("SnackbarHostState not provided")
}
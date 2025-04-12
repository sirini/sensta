package me.sensta.viewmodel.common

import androidx.compose.runtime.staticCompositionLocalOf
import me.sensta.viewmodel.AuthViewModel

val LocalAuthViewModel = staticCompositionLocalOf<AuthViewModel> {
    error("No authViewModel provided")
}
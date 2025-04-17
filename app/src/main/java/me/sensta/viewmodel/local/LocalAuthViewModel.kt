package me.sensta.viewmodel.local

import androidx.compose.runtime.staticCompositionLocalOf
import me.sensta.viewmodel.AuthViewModel

val LocalAuthViewModel = staticCompositionLocalOf<AuthViewModel> {
    error("No authViewModel provided")
}
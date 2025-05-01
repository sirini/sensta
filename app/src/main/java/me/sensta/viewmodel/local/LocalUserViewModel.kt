package me.sensta.viewmodel.local

import androidx.compose.runtime.staticCompositionLocalOf
import me.sensta.viewmodel.UserViewModel

val LocalUserViewModel = staticCompositionLocalOf<UserViewModel> {
    error("No UserViewModel provided")
}
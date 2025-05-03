package me.sensta.viewmodel.local

import androidx.compose.runtime.staticCompositionLocalOf
import me.sensta.viewmodel.UserChatViewModel

val LocalUserChatViewModel = staticCompositionLocalOf<UserChatViewModel> {
    error("No UserViewModel provided")
}
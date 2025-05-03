package me.sensta.viewmodel.local

import androidx.compose.runtime.staticCompositionLocalOf
import me.sensta.viewmodel.PostViewViewModel

val LocalPostViewViewModel = staticCompositionLocalOf<PostViewViewModel> {
    error("No PostViewViewModel provided")
}
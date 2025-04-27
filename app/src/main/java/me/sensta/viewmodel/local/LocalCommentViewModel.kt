package me.sensta.viewmodel.local

import androidx.compose.runtime.staticCompositionLocalOf
import me.sensta.viewmodel.CommentViewModel

val LocalCommentViewModel = staticCompositionLocalOf<CommentViewModel> {
    error("No CommentViewModel provided")
}
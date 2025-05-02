package me.sensta.viewmodel.local

import androidx.compose.runtime.staticCompositionLocalOf
import me.sensta.viewmodel.ExplorerViewModel

val LocalExplorerViewModel = staticCompositionLocalOf<ExplorerViewModel> {
    error("No ExplorerViewModel provided")
}
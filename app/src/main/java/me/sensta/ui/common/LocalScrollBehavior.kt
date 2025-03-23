package me.sensta.ui.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.staticCompositionLocalOf

@OptIn(ExperimentalMaterial3Api::class)
val LocalScrollBehavior = staticCompositionLocalOf<TopAppBarScrollBehavior> {
    error("No LocalScrollBehavior provided")
}
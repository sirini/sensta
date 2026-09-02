package me.sensta.viewmodel.local

import androidx.compose.runtime.staticCompositionLocalOf
import me.sensta.viewmodel.AchievementViewModel

val LocalAchievementViewModel = staticCompositionLocalOf<AchievementViewModel> {
    error("No achievementViewModel provided")
}

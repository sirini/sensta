package me.sensta.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import me.sensta.ui.screen.profile.ProfileView
import me.sensta.viewmodel.common.LocalAuthViewModel
import me.sensta.viewmodel.common.LocalNotificationViewModel

@Composable
fun ProfileScreen() {
    val authViewModel = LocalAuthViewModel.current
    val notiViewModel = LocalNotificationViewModel.current
    val user by authViewModel.user.collectAsState()
    val isLoading by authViewModel.isLoading

    LaunchedEffect(Unit) {
        notiViewModel.loadNotifications()
    }

    when {
        isLoading -> LoadingScreen()
        user.token.isEmpty() -> LoginScreen()
        else -> ProfileView()
    }
}
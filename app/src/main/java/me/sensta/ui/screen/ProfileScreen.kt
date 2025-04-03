package me.sensta.ui.screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import me.sensta.ui.screen.profile.ProfileView
import me.sensta.viewmodel.AuthViewModel

@Composable
fun ProfileScreen() {
    val authViewModel: AuthViewModel = hiltViewModel()

    when {
        authViewModel.isLoading -> LoadingScreen()
        authViewModel.user.token.isEmpty() -> LoginScreen()
        else -> ProfileView()
    }
}
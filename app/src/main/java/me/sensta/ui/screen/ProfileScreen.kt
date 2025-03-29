package me.sensta.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import me.sensta.ui.screen.profile.ProfileView
import me.sensta.viewmodel.AuthViewModel

@Composable
fun ProfileScreen() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val user by authViewModel.user.collectAsState()

    when {
        authViewModel.isLoading -> LoadingScreen()
        user.token.isEmpty() -> LoginScreen()
        else -> ProfileView()
    }
}
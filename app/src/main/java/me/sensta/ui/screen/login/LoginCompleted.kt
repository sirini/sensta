package me.sensta.ui.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.data.env.Env
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.theme.robotoSlabFontFamily
import me.sensta.viewmodel.local.LocalAuthViewModel

@Composable
fun LoginCompleted() {
    val navController = LocalNavController.current
    val authViewModel = LocalAuthViewModel.current
    val user by authViewModel.user

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "WELCOME BACK",
            modifier = Modifier.padding(bottom = 16.dp),
            fontFamily = robotoSlabFontFamily,
        )

        if (user.profile.isNotEmpty()) {
            AsyncImage(
                model = Env.DOMAIN + user.profile,
                contentDescription = user.name,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "user empty profile image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            )
        }

        Text(
            text = "다시 오신 것을 환영합니다, ${user.name}님!",
            modifier = Modifier
                .padding(top = 24.dp)
                .align(alignment = Alignment.CenterHorizontally)
        )

        Button(
            onClick = {
                navController.navigate(Screen.Home.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 4.dp)
        ) {
            Text(text = "홈 화면으로 이동하기")
        }

        TextButton(
            onClick = {
                navController.navigate(Screen.Profile.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "내 프로필 확인하기")
        }
    }
}
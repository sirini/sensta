package me.sensta.ui.screen.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.theme.robotoSlabFontFamily
import me.sensta.viewmodel.local.LocalAuthViewModel

@Composable
fun SignupCompleted() {
    val navController = LocalNavController.current
    val authViewModel = LocalAuthViewModel.current
    val name by authViewModel.name

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "WELCOME TO SENSTA!",
            modifier = Modifier.padding(bottom = 16.dp),
            fontFamily = robotoSlabFontFamily
        )

        Icon(
            imageVector = Icons.Default.CameraRoll,
            contentDescription = "Camera icon",
            modifier = Modifier
                .size(80.dp)
                .padding(top = 8.dp)
        )

        Text(
            text = "진짜 사진을 만나는 곳, SENSTA에 가입하신 사진가님 환영합니다!",
            modifier = Modifier
                .padding(top = 24.dp)
                .align(alignment = Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = {
                navController.navigate(Screen.Home.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            }, modifier = Modifier.weight(1f)) {
                Text(text = "홈으로")
            }

            Button(
                onClick = {
                    navController.navigate(Screen.Login.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }, modifier = Modifier
                    .weight(2f)
                    .padding(start = 8.dp)
            ) {
                Text(text = "로그인 하기")
            }
        }
    }
}
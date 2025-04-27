package me.sensta.ui.screen.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import me.sensta.ui.theme.robotoSlabFontFamily
import me.sensta.viewmodel.SignupState
import me.sensta.viewmodel.local.LocalAuthViewModel

@Composable
fun SignupInputName() {
    val context = LocalContext.current
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
            text = "ENTER YOUR NAME",
            modifier = Modifier.padding(bottom = 16.dp),
            fontFamily = robotoSlabFontFamily
        )

        OutlinedTextField(
            value = name,
            onValueChange = { authViewModel.setName(it) },
            label = { Text(text = "이름을 입력하세요") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = {
                authViewModel.setSignupState(SignupState.InputPassword)
            }, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "뒤로")
            }

            Button(
                onClick = { authViewModel.checkValidName(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .padding(start = 16.dp)
            ) {
                Text(text = "다음 단계로 이동하기")
            }
        }
    }
}
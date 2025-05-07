package me.sensta.ui.screen.login

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
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import me.sensta.ui.theme.robotoSlabFontFamily
import me.sensta.viewmodel.local.LocalAuthViewModel

@Composable
fun LoginInputPassword() {
    val authViewModel = LocalAuthViewModel.current
    val pw by authViewModel.pw
    var isPwVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "PASSWORD",
            modifier = Modifier.padding(bottom = 16.dp),
            fontFamily = robotoSlabFontFamily,
        )

        OutlinedTextField(
            value = pw,
            onValueChange = { authViewModel.setPW(it) },
            label = { Text(text = "비밀번호를 입력하세요") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Password,
                    contentDescription = "Password"
                )
            },
            singleLine = true,
            visualTransformation = when (isPwVisible) {
                true -> VisualTransformation.None
                false -> PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { isPwVisible = !isPwVisible }) {
                    Icon(
                        imageVector = if (isPwVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Password"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { authViewModel.login() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "로그인 하기", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { authViewModel.backToID() }, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                    contentDescription = "back",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "뒤로")
            }

            TextButton(onClick = {}, modifier = Modifier.weight(2f)) {
                Icon(
                    imageVector = Icons.Default.LockReset,
                    contentDescription = "Join",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "비밀번호 초기화")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
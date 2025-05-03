package me.sensta.ui.screen.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import me.sensta.ui.theme.robotoSlabFontFamily
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.state.SignupState

@Composable
fun SignupInputPassword() {
    val context = LocalContext.current
    val authViewModel = LocalAuthViewModel.current
    val pw by authViewModel.pw
    var pwAgain by remember { mutableStateOf("") }
    var isPwVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ENTER YOUR PASSWORD",
            modifier = Modifier.padding(bottom = 16.dp),
            fontFamily = robotoSlabFontFamily,
        )

        OutlinedTextField(
            value = pw,
            onValueChange = { authViewModel.setPW(it) },
            label = { Text(text = "비밀번호를 입력하세요") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Password,
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

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = pwAgain,
            onValueChange = { pwAgain = it.trim() },
            label = { Text(text = "한번 더 입력해주세요") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "Password again"
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

        SignupBottomRow(onBack = { authViewModel.setSignupState(SignupState.InputEmail) }) {
            authViewModel.checkValidPW(context, pwAgain)
        }
    }
}
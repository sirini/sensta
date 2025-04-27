package me.sensta.ui.screen.signup

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import me.sensta.ui.theme.robotoSlabFontFamily
import me.sensta.viewmodel.SignupState
import me.sensta.viewmodel.local.LocalAuthViewModel

@Composable
fun SignupInputCode() {
    val context = LocalContext.current
    val authViewModel = LocalAuthViewModel.current
    val id by authViewModel.id
    val otp by authViewModel.otp
    val otpMaxLength = 6

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ENTER VERIFICATION CODE",
            modifier = Modifier.padding(bottom = 16.dp),
            fontFamily = robotoSlabFontFamily
        )

        Text(
            text = "이메일로 전송한 코드 6자리를 아래에 입력해 주세요",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box {
            BasicTextField(
                value = otp,
                onValueChange = {
                    if (it.length <= otpMaxLength) {
                        authViewModel.setOTP(it)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 16.dp)
            ) {
                repeat(otpMaxLength) { index ->
                    val char = otp.getOrNull(index)?.toString() ?: ""
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(42.dp)
                            .border(
                                width = 2.dp,
                                color = if (char.isNotEmpty()) MaterialTheme.colorScheme.background.copy(
                                    alpha = 0.3f
                                ) else MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.1f
                                ),
                                shape = MaterialTheme.shapes.small
                            )
                    ) {
                        if (index == otp.length && char.isEmpty()) {
                            CursorBlinking()
                        } else {
                            Text(text = char, style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = {
                authViewModel.setSignupState(SignupState.InputName)
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
                onClick = { authViewModel.checkVerificationCode(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .padding(start = 16.dp)
            ) {
                Text(text = "인증 완료하기")
            }
        }
    }
}

@Composable
fun CursorBlinking() {
    val visible = remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            visible.value = !visible.value
        }
    }
    if (visible.value) {
        Text(
            "|",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }
}
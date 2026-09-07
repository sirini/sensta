package me.sensta.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.MarkEmailRead
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.sensta.viewmodel.local.LocalAuthViewModel

@Composable
fun PasswordResetScreen() {
    val authViewModel = LocalAuthViewModel.current
    val requested by authViewModel.passwordResetRequested
    val isLoading by authViewModel.isPasswordResetLoading
    val error by authViewModel.passwordResetError
    var email by rememberSaveable { mutableStateOf(authViewModel.id.value) }

    LaunchedEffect(Unit) { authViewModel.resetPasswordResetFlow() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            if (isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (requested) Icons.Outlined.MarkEmailRead else Icons.Outlined.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = if (requested) "메일을 확인해 주세요" else "비밀번호 재설정",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (requested) {
                        "입력한 주소가 SENSTA 계정에 등록되어 있다면 재설정 링크를 보냈습니다. 링크는 10분 동안 한 번만 사용할 수 있습니다."
                    } else {
                        "가입할 때 사용한 이메일로 재설정 링크를 보내드립니다."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                if (requested) {
                    Spacer(Modifier.height(16.dp))
                    TextButton(onClick = {
                        authViewModel.resetPasswordResetFlow()
                        email = ""
                    }) {
                        Text("다른 이메일 사용")
                    }
                } else {
                    Spacer(Modifier.height(20.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("이메일 주소") },
                        singleLine = true,
                        enabled = !isLoading,
                        isError = error != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    error?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { authViewModel.requestPasswordReset(email) },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("재설정 메일 보내기")
                    }
                }
            }
        }
    }
}

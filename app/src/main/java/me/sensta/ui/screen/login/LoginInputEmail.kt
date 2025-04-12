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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Email
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
import me.sensta.viewmodel.common.LocalAuthViewModel

@Composable
fun LoginInputEmail() {
    val context = LocalContext.current
    val authViewModel = LocalAuthViewModel.current
    val id by authViewModel.id
    val isEmailValid by authViewModel.isEmailValid

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "EMAIL",
            modifier = Modifier.padding(bottom = 16.dp),
            fontFamily = robotoSlabFontFamily,
        )

        OutlinedTextField(
            value = id,
            onValueChange = { authViewModel.setID(it) },
            label = { Text(text = "이메일 주소를 입력하세요") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email"
                )
            },
            singleLine = true,
            isError = id.isNotEmpty() && !isEmailValid
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                TextButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = "Join",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "회원가입")
                }
            }

            Row {
                Button(onClick = { authViewModel.checkValidID(context = context) }) {
                    Text(text = "다음")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                        contentDescription = "Next",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
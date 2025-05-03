package me.sensta.ui.screen.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.theme.robotoSlabFontFamily
import me.sensta.viewmodel.local.LocalAuthViewModel

@Composable
fun SignupInputEmail() {
    val navController = LocalNavController.current
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
            text = "ENTER YOUR EMAIL",
            modifier = Modifier.padding(bottom = 16.dp),
            fontFamily = robotoSlabFontFamily,
        )

        OutlinedTextField(
            value = id,
            onValueChange = { authViewModel.setID(it) },
            label = { Text(text = "이메일 주소를 입력하세요") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = "Email"
                )
            },
            singleLine = true,
            isError = id.isNotEmpty() && !isEmailValid
        )

        Spacer(modifier = Modifier.height(32.dp))

        SignupBottomRow(onBack = {
            navController.navigate("login") {
                launchSingleTop = true
                restoreState = true
            }
        }) {
            authViewModel.checkValidID(isSignup = true)
        }
    }
}
package me.sensta.ui.screen.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import me.sensta.ui.theme.robotoSlabFontFamily
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.state.SignupState

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

        SignupBottomRow(onBack = { authViewModel.setSignupState(SignupState.InputPassword) }) {
            authViewModel.checkValidName()
        }
    }
}
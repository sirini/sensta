package me.sensta.ui.screen.signup

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Checkbox
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import me.data.env.Env
import me.sensta.ui.theme.robotoSlabFontFamily
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.state.SignupState

@Composable
fun SignupInputName() {
    val context = LocalContext.current
    val authViewModel = LocalAuthViewModel.current
    val name by authViewModel.name
    val invite by authViewModel.invite
    val signupStatus by authViewModel.signupStatus
    val isPolicyAccepted by authViewModel.isCommunityPolicyAccepted

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

        if (signupStatus?.requiresInvite == true) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = invite,
                onValueChange = authViewModel::setInvite,
                label = { Text(text = "초대 코드") },
                singleLine = true
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isPolicyAccepted,
                onCheckedChange = authViewModel::acceptCommunityPolicy
            )
            Text("이용약관과 커뮤니티 운영 원칙에 동의합니다")
        }
        Row(horizontalArrangement = Arrangement.Center) {
            TextButton(onClick = {
                context.startActivity(Intent(Intent.ACTION_VIEW, "${Env.DOMAIN}/terms".toUri()))
            }) {
                Text("이용약관")
            }
            TextButton(onClick = {
                context.startActivity(Intent(Intent.ACTION_VIEW, "${Env.DOMAIN}/privacy".toUri()))
            }) {
                Text("개인정보 처리방침")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SignupBottomRow(onBack = { authViewModel.setSignupState(SignupState.InputPassword) }) {
            authViewModel.checkValidName()
        }
    }
}

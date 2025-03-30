package me.sensta.ui.screen.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import me.sensta.util.Format
import me.sensta.viewmodel.AuthViewModel
import java.util.Locale

@Composable
fun ProfileView() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val user by authViewModel.user.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileViewImage()

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "기본 정보",
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 8.dp),
                style = MaterialTheme.typography.bodySmall
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ProfileViewItem(name = "아이디", value = user.id)
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                )
                ProfileViewItem(name = "이름", value = user.name) {
                    authViewModel.openEditNameDialog()
                }
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                )
                ProfileViewItem(name = "레벨", value = user.level.toString())
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                )
                ProfileViewItem(
                    name = "포인트",
                    value = String.format(locale = Locale.KOREAN, format = "%,d", user.point)
                )
            }

            Text(
                text = "서명 정보",
                modifier = Modifier.padding(top = 32.dp, bottom = 8.dp, start = 8.dp),
                style = MaterialTheme.typography.bodySmall
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = user.signature,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Text(
                text = "계정 정보",
                modifier = Modifier.padding(top = 32.dp, bottom = 8.dp, start = 8.dp),
                style = MaterialTheme.typography.bodySmall
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ProfileViewItem(name = "가입일", value = user.signup.format(Format.simpleDate))
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                )
                ProfileViewItem(name = "로그인", value = user.signin.format(Format.fullDate))
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                )
                ProfileViewItem(name = "관리자", value = if (user.admin) "관리자님, 환영합니다" else "일반 회원")
            }
        }

        if (authViewModel.editNameDialog) {
            ProfileViewEditNameDialog(
                onDismissRequest = { authViewModel.closeEditNameDialog() },
                onConfirm = { /* TODO */ }
            )
        }
    }
}
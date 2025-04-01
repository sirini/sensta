package me.sensta.ui.screen.profile

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import me.sensta.util.Format
import me.sensta.viewmodel.AuthViewModel
import java.util.Locale

@Composable
fun ProfileView() {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = hiltViewModel()
    val user by authViewModel.user.collectAsState()

    var isEditNameDialog by remember { mutableStateOf(false) }
    var isEditSignatureDialog by remember { mutableStateOf(false) }

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
                ProfileViewItem(name = "아이디", value = user.id) {
                    Toast.makeText(context, "아이디는 수정할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                )
                ProfileViewItem(name = "이름", value = user.name) {
                    isEditNameDialog = !isEditNameDialog
                }
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                )
                ProfileViewItem(name = "레벨", value = user.level.toString()) {
                    Toast.makeText(context, "레벨은 오직 관리자만 변경 가능합니다.", Toast.LENGTH_SHORT).show()
                }
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                )
                ProfileViewItem(
                    name = "포인트",
                    value = String.format(locale = Locale.KOREAN, format = "%,d", user.point)
                ) {
                    Toast.makeText(context, "포인트는 임의로 수정할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            Text(
                text = "서명 정보",
                modifier = Modifier.padding(top = 32.dp, bottom = 8.dp, start = 8.dp),
                style = MaterialTheme.typography.bodySmall
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isEditSignatureDialog = !isEditSignatureDialog }
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
                ProfileViewItem(name = "가입일", value = user.signup.format(Format.simpleDate)) {
                    Toast.makeText(
                        context,
                        "최초 가입일은 ${user.signup.format(Format.fullDate)} 입니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                )
                ProfileViewItem(name = "로그인", value = user.signin.format(Format.fullDate)) {
                    Toast.makeText(context, "마지막으로 로그인 하신 시간입니다.", Toast.LENGTH_SHORT).show()
                }
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                )
                ProfileViewItem(name = "관리자", value = if (user.admin) "관리자님, 환영합니다" else "일반 회원")
            }
        }

        if (isEditNameDialog) {
            ProfileViewEditNameDialog(
                onDismissRequest = { isEditNameDialog = !isEditNameDialog },
                onConfirm = {
                    authViewModel.updateName(it)
                    isEditNameDialog = !isEditNameDialog
                }
            )
        }

        if (isEditSignatureDialog) {
            ProfileViewEditSignatureDialog(
                onDismissRequest = { isEditSignatureDialog = !isEditSignatureDialog },
                onConfirm = {
                    authViewModel.updateSignature(it)
                    isEditSignatureDialog = !isEditSignatureDialog
                }
            )
        }
    }
}
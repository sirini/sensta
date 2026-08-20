package me.sensta.ui.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.data.env.Env
import me.sensta.ui.theme.robotoSlabFontFamily
import me.sensta.util.CustomTime
import me.sensta.viewmodel.local.LocalUserChatViewModel
import me.sensta.ui.common.CommonDialog
import me.sensta.ui.common.UserReportDialog

@Composable
fun OtherUserInfo() {
    val userViewModel = LocalUserChatViewModel.current
    val otherUser by userViewModel.otherUser
    val isLoading by userViewModel.isLoadingInfo
    val isReported by userViewModel.isReported
    val isBlockedByMe by userViewModel.isBlockedByMe
    var showReportDialog by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.large
    ) {
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                AsyncImage(
                    model = Env.DOMAIN + otherUser.profile,
                    contentDescription = otherUser.name,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                )
                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(text = otherUser.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "마지막 로그인: ${otherUser.signin.format(CustomTime.fullDate)}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Row {
                if (otherUser.admin) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "admin",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "관리자",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                } else {
                    Text(
                        text = "Lv. 1",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(end = 8.dp),
                        fontFamily = robotoSlabFontFamily
                    )
                }
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (otherUser.signature.isNotEmpty()) {
                Text(text = otherUser.signature)
            } else {
                Text(text = "작성된 서명이 없습니다")
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { showReportDialog = true },
                enabled = !isReported,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Outlined.Report, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isReported) "신고 접수됨" else "사용자 신고")
            }
            OutlinedButton(
                onClick = { showBlockDialog = true },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Outlined.Block, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isBlockedByMe) "차단 해제" else "사용자 차단")
            }
        }
    }

    if (showReportDialog) {
        UserReportDialog(
            title = "${otherUser.name}님 신고",
            description = "운영진이 확인할 수 있도록 문제가 된 행동을 구체적으로 적어주세요.",
            onDismissRequest = { showReportDialog = false },
            onReport = { reason ->
                userViewModel.reportUser(otherUser.uid, "사용자 신고: $reason")
                showReportDialog = false
            }
        )
    }

    if (showBlockDialog) {
        CommonDialog(
            onDismissRequest = { showBlockDialog = false },
            onConfirm = {
                userViewModel.changeBlockStatus()
                showBlockDialog = false
            },
            icon = Icons.Outlined.Block,
            content = {
                Text(
                    if (isBlockedByMe) {
                        "${otherUser.name}님의 차단을 해제하고 대화를 다시 표시할까요?"
                    } else {
                        "${otherUser.name}님을 차단하면 사진과 대화를 더 이상 표시하지 않습니다."
                    }
                )
            }
        )
    }
}

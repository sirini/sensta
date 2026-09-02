package me.sensta.ui.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Report
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.data.env.Env
import me.sensta.ui.common.CommonDialog
import me.sensta.ui.common.AchievementShelf
import me.sensta.ui.common.UserReportDialog
import me.sensta.ui.theme.LocalSenstaExtendedColors
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.local.LocalUserChatViewModel

@Composable
fun OtherUserInfo(latestPhoto: String?) {
    val authViewModel = LocalAuthViewModel.current
    val userViewModel = LocalUserChatViewModel.current
    val my by authViewModel.user
    val otherUser by userViewModel.otherUser
    val isLoading by userViewModel.isLoadingInfo
    val isReported by userViewModel.isReported
    val isBlockedByMe by userViewModel.isBlockedByMe
    val onMedia = LocalSenstaExtendedColors.current.onMedia
    val isMyProfile = my.uid > 0 && my.uid == otherUser.uid
    var showReportDialog by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            if (!latestPhoto.isNullOrBlank()) {
                AsyncImage(
                    model = Env.DOMAIN + latestPhoto,
                    contentDescription = "${otherUser.name}님의 최근 사진",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f))
                            )
                        )
                )
            }

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            AsyncImage(
                model = Env.DOMAIN + otherUser.profile,
                contentDescription = otherUser.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 44.dp)
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .border(3.dp, MaterialTheme.colorScheme.background, CircleShape)
            )

            if (!latestPhoto.isNullOrBlank()) {
                Text(
                    text = "LATEST WORK",
                    style = MaterialTheme.typography.labelSmall,
                    color = onMedia.copy(alpha = 0.76f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(52.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = otherUser.name, style = MaterialTheme.typography.headlineSmall)
            if (otherUser.admin) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "관리자",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Text(
            text = otherUser.signature.ifBlank { "사진으로 이야기를 나누는 Sensta 포토그래퍼" },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
        )

        AchievementShelf(
            badges = otherUser.badges,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
        )

        if (!isMyProfile) {
            Row(
                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showReportDialog = true },
                    enabled = !isReported
                ) {
                    Icon(Icons.Outlined.Report, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(if (isReported) "신고됨" else "신고")
                }
                OutlinedButton(onClick = { showBlockDialog = true }) {
                    Icon(Icons.Outlined.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(if (isBlockedByMe) "차단 해제" else "차단")
                }
            }
        } else {
            Spacer(modifier = Modifier.height(12.dp))
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

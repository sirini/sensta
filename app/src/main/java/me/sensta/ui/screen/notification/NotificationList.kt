package me.sensta.ui.screen.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.domain.model.home.NotificationType
import me.domain.model.home.TsboardNotification
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.viewmodel.local.LocalCommonViewModel
import me.sensta.viewmodel.local.LocalNotificationViewModel
import me.sensta.viewmodel.local.LocalUserChatViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationList(notifications: List<TsboardNotification>) {
    val navController = LocalNavController.current
    val notiViewModel = LocalNotificationViewModel.current
    val userViewModel = LocalUserChatViewModel.current
    val isLoading by notiViewModel.isLoading
    val commonViewModel = LocalCommonViewModel.current
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = { notiViewModel.loadNotifications() }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        if (notifications.isEmpty()) {
            NotificationEmpty()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Card(modifier = Modifier.fillMaxWidth()) {
                    notifications.forEach { notification ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (notification.type == NotificationType.NOTI_CHAT_MESSAGE) {
                                        userViewModel.loadOtherUserInfo(notification.fromUser)
                                        navController.navigate(Screen.User.route) {
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    } else {
                                        commonViewModel.apply {
                                            updatePagerIndex(0)
                                            updatePostUid(notification.postUid)
                                        }
                                        notiViewModel.checkNotification(notification.uid)
                                        navController.navigate(Screen.View.route) {
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val translated =
                                    notiViewModel.translateNotification(notification.type)

                                Icon(
                                    imageVector = translated.first,
                                    contentDescription = "notification icon",
                                    modifier = Modifier
                                        .padding(end = 12.dp)
                                )

                                Text(
                                    text = "${notification.fromUser.name}님이 ${translated.second}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (notification.checked) {
                                        FontWeight.Thin
                                    } else {
                                        FontWeight.Bold
                                    }
                                )
                            }
                        }
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
                        )
                    }
                }

                Button(
                    onClick = { notiViewModel.checkAllNotifications() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Checklist,
                        contentDescription = "check all"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "모두 읽음으로 처리")
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // 당겨서 새로고침 중일 때 로딩 화면 제공
        PullRefreshIndicator(
            refreshing = isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
package me.sensta.ui.screen

import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import me.sensta.ui.common.LocalScrollBehavior
import me.sensta.ui.screen.profile.ProfileView
import me.sensta.ui.screen.profile.ProfileTab
import me.sensta.viewmodel.ProfileStudioViewModel
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.local.LocalAchievementViewModel
import me.sensta.viewmodel.local.LocalNotificationViewModel
import me.sensta.viewmodel.uievent.ProfileUiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val scrollBehavior = LocalScrollBehavior.current
    val authViewModel = LocalAuthViewModel.current
    val achievementViewModel = LocalAchievementViewModel.current
    val notiViewModel = LocalNotificationViewModel.current
    val studioViewModel: ProfileStudioViewModel = hiltViewModel()
    val user by authViewModel.user
    val isLoading by authViewModel.isLoading
    val studio by studioViewModel.uiState.collectAsState()
    val achievements by achievementViewModel.profileBadges
    val openAchievementTab by achievementViewModel.profileAchievementTabRequested
    val selectedTabIndex = rememberSaveable { mutableIntStateOf(ProfileTab.Works.ordinal) }

    LaunchedEffect(openAchievementTab) {
        if (openAchievementTab) {
            selectedTabIndex.intValue = ProfileTab.Achievements.ordinal
            achievementViewModel.consumeProfileAchievementTabRequest()
        }
    }

    LaunchedEffect(user.uid, user.token) {
        if (user.token.isNotBlank()) {
            studioViewModel.refresh()
            achievementViewModel.loadProfileAchievements(user.uid)
        }
    }

    LaunchedEffect(Unit) {
        // 스크롤 상태를 초기화해서 topBar가 펼쳐진 상태로 만들기
        scrollBehavior.state.heightOffset = 0f

        // 알림 내용 가져오기
        notiViewModel.loadNotifications()

        // AuthViewModel에서 전달된 이벤트들 중 프로필과 연관된 내용들 출력하기
        authViewModel.uiProfileEvent.collect { event ->
            when (event) {
                is ProfileUiEvent.ProfileImageUpdated -> {
                    Toast.makeText(context, "프로필 이미지가 변경되었습니다", Toast.LENGTH_SHORT).show()
                }

                is ProfileUiEvent.FailedToUpdateProfileImage -> {
                    Toast.makeText(
                        context,
                        "프로필 이미지 변경에 실패했습니다 (${event.message})",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is ProfileUiEvent.ChangedName -> {
                    Toast.makeText(context, "닉네임이 변경되었습니다", Toast.LENGTH_SHORT).show()
                }

                is ProfileUiEvent.FailedToChangeName -> {
                    Toast.makeText(
                        context,
                        "닉네임 변경에 실패했습니다 (${event.message})",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is ProfileUiEvent.ChangedSignature -> {
                    Toast.makeText(context, "서명이 변경되었습니다", Toast.LENGTH_SHORT).show()
                }

                is ProfileUiEvent.FailedToChangeSignature -> {
                    Toast.makeText(
                        context,
                        "서명 변경에 실패했습니다 (${event.message})",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is ProfileUiEvent.AccountDeleted -> {
                    Toast.makeText(context, "계정과 모든 데이터가 삭제되었습니다", Toast.LENGTH_LONG).show()
                }

                is ProfileUiEvent.FailedToDeleteAccount -> {
                    Toast.makeText(
                        context,
                        "계정 삭제에 실패했습니다 (${event.message})",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    when {
        isLoading -> LoadingScreen()
        user.token.isEmpty() -> LoginScreen()
        else -> ProfileView(
            studio = studio,
            achievements = achievements,
            selectedTab = ProfileTab.entries[selectedTabIndex.intValue],
            onSelectTab = { selectedTabIndex.intValue = it.ordinal },
            onRefreshStudio = studioViewModel::refresh,
            onLoadMoreStudio = studioViewModel::loadMore,
            onSelectSort = studioViewModel::selectSort
        )
    }
}

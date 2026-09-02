package me.sensta.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.domain.model.common.NuboBadge
import me.domain.repository.handle
import me.domain.usecase.auth.AcknowledgeAchievementsUseCase
import me.domain.usecase.auth.GetUnannouncedAchievementsUseCase
import me.domain.usecase.user.GetOtherUserInfoUseCase
import javax.inject.Inject

@HiltViewModel
class AchievementViewModel @Inject constructor(
    private val getUnannouncedAchievementsUseCase: GetUnannouncedAchievementsUseCase,
    private val acknowledgeAchievementsUseCase: AcknowledgeAchievementsUseCase,
    private val getOtherUserInfoUseCase: GetOtherUserInfoUseCase
) : ViewModel() {
    private val _queue = mutableStateOf<List<NuboBadge>>(emptyList())
    val queue: State<List<NuboBadge>> get() = _queue

    private val _profileBadges = mutableStateOf<List<NuboBadge>>(emptyList())
    val profileBadges: State<List<NuboBadge>> get() = _profileBadges

    private var checking = false
    private var acknowledging = false
    private var profileUserUid = 0

    fun check(token: String) {
        if (token.isBlank() || checking || acknowledging) return
        viewModelScope.launch {
            checking = true
            try {
                getUnannouncedAchievementsUseCase(token).handle { badges ->
                    _queue.value = badges
                    if (badges.isNotEmpty()) {
                        _profileBadges.value = (_profileBadges.value + badges).distinctBy { it.key }
                    }
                }
            } finally {
                checking = false
            }
        }
    }

    fun loadProfileAchievements(userUid: Int) {
        if (userUid < 1) {
            profileUserUid = 0
            _profileBadges.value = emptyList()
            return
        }
        profileUserUid = userUid
        viewModelScope.launch {
            getOtherUserInfoUseCase(userUid).collect { result ->
                result.handle { user ->
                    if (profileUserUid == userUid) _profileBadges.value = user.badges
                }
            }
        }
    }

    fun acknowledgeCurrent(token: String, onAcknowledged: () -> Unit = {}) {
        val current = _queue.value.firstOrNull() ?: return
        if (token.isBlank() || acknowledging) return
        viewModelScope.launch {
            acknowledging = true
            try {
                acknowledgeAchievementsUseCase(token, listOf(current.key)).handle { response ->
                    if (response.success) {
                        _queue.value = _queue.value.drop(1)
                        onAcknowledged()
                    }
                }
            } finally {
                acknowledging = false
            }
        }
    }
}

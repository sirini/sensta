package me.domain.usecase.auth

import me.domain.repository.NuboAuthRepository
import javax.inject.Inject

class AcknowledgeAchievementsUseCase @Inject constructor(
    private val repository: NuboAuthRepository
) {
    suspend operator fun invoke(token: String, keys: List<String>) =
        repository.acknowledgeAchievements(token, keys)
}

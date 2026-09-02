package me.domain.usecase.auth

import me.domain.repository.NuboAuthRepository
import javax.inject.Inject

class GetUnannouncedAchievementsUseCase @Inject constructor(
    private val repository: NuboAuthRepository
) {
    suspend operator fun invoke(token: String) = repository.getUnannouncedAchievements(token)
}

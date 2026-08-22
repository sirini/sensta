package me.domain.usecase.home

import me.domain.repository.NuboNotificationRepository
import javax.inject.Inject

// 로그아웃하는 기기를 푸시 알림 수신 대상에서 해제한다.
class UnregisterPushDeviceUseCase @Inject constructor(
    private val repository: NuboNotificationRepository
) {
    suspend operator fun invoke(deviceToken: String, token: String) =
        repository.unregisterPushDevice(deviceToken = deviceToken, token = token)
}

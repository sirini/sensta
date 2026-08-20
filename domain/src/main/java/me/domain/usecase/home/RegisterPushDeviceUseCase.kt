package me.domain.usecase.home

import me.domain.repository.TsboardNotificationRepository
import javax.inject.Inject

// 현재 기기를 푸시 알림 수신 대상으로 등록한다.
class RegisterPushDeviceUseCase @Inject constructor(
    private val repository: TsboardNotificationRepository
) {
    suspend operator fun invoke(deviceToken: String, token: String) =
        repository.registerPushDevice(deviceToken = deviceToken, token = token)
}

package me.data.repository

import me.data.remote.api.NuboUserApi
import me.data.remote.dto.user.toEntity
import me.data.remote.dto.user.SendChatRequestDto
import me.data.remote.dto.user.UserReportRequestDto
import me.data.remote.dto.user.UserTargetRequestDto
import me.data.remote.dto.common.toEntity
import me.domain.model.common.NuboResponseNothing
import me.domain.model.user.NuboChatHistoryResponse
import me.domain.model.user.NuboOtherUserInfoResult
import me.domain.model.user.NuboSendChatResponse
import me.domain.model.user.NuboUserSafetyStatus
import me.domain.repository.NuboResponse
import me.domain.repository.NuboUserChatRepository
import javax.inject.Inject

class NuboUserChatRepositoryImpl @Inject constructor(
    private val api: NuboUserApi
) : NuboUserChatRepository {

    // 다른 사용자의 기본 정보 가져오기
    override suspend fun getOtherUserInfo(
        userUid: Int
    ): NuboResponse<NuboOtherUserInfoResult> {
        return try {
            val response = api.getOtherUserInfo(targetUserUid = userUid)
            val result = response.result
            if (!response.success || result == null) {
                NuboResponse.Error(response.error.ifBlank { "사용자 정보를 찾지 못했습니다" })
            } else {
                NuboResponse.Success(result.toEntity())
            }
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    // 상대방과의 대화 기록을 가져오기
    override suspend fun getChatHistory(
        targetUserUid: Int,
        limit: Int,
        token: String
    ): NuboResponse<NuboChatHistoryResponse> {
        return try {
            val response = api.getChatHistory(
                authorization = "Bearer $token",
                targetUserUid = targetUserUid,
                limit = limit
            )
            NuboResponse.Success(response.toEntity())
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    // 상대방에게 메시지 보내기
    override suspend fun sendChatMessage(
        targetUserUid: Int,
        message: String,
        token: String
    ): NuboResponse<NuboSendChatResponse> {
        return try {
            val response = api.sendChatMessage(
                authorization = "Bearer $token",
                request = SendChatRequestDto(
                    targetUserUid = targetUserUid,
                    message = message
                )
            )
            NuboResponse.Success(response.toEntity())
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    override suspend fun getUserSafetyStatus(
        targetUserUid: Int,
        token: String
    ): NuboResponse<NuboUserSafetyStatus> {
        return try {
            val response = api.getUserSafetyStatus("Bearer $token", targetUserUid)
            val result = response.result
            if (!response.success || result == null) {
                NuboResponse.Error(response.error.ifBlank { "안전 설정을 불러오지 못했습니다" })
            } else {
                NuboResponse.Success(result.toEntity())
            }
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "안전 설정을 불러오지 못했습니다", cause = e)
        }
    }

    override suspend fun reportUser(
        targetUserUid: Int,
        content: String,
        token: String
    ): NuboResponse<NuboResponseNothing> {
        return try {
            NuboResponse.Success(
                api.reportUser(
                    "Bearer $token",
                    UserReportRequestDto(
                        targetUserUid = targetUserUid,
                        checkedBlackList = false,
                        content = content
                    )
                ).toEntity()
            )
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "신고를 접수하지 못했습니다", cause = e)
        }
    }

    override suspend fun blockUser(
        targetUserUid: Int,
        token: String
    ): NuboResponse<NuboResponseNothing> = changeBlockStatus(targetUserUid, token, true)

    override suspend fun unblockUser(
        targetUserUid: Int,
        token: String
    ): NuboResponse<NuboResponseNothing> = changeBlockStatus(targetUserUid, token, false)

    private suspend fun changeBlockStatus(
        targetUserUid: Int,
        token: String,
        blocked: Boolean
    ): NuboResponse<NuboResponseNothing> {
        return try {
            val request = UserTargetRequestDto(targetUserUid)
            val response = if (blocked) {
                api.blockUser("Bearer $token", request)
            } else {
                api.unblockUser("Bearer $token", request)
            }
            NuboResponse.Success(response.toEntity())
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "차단 설정을 변경하지 못했습니다", cause = e)
        }
    }
}

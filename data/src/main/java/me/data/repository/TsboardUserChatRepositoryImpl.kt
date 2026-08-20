package me.data.repository

import me.data.remote.api.TsboardGoapi
import me.data.remote.dto.user.toEntity
import me.data.remote.dto.user.SendChatRequestDto
import me.data.remote.dto.user.UserReportRequestDto
import me.data.remote.dto.user.UserTargetRequestDto
import me.data.remote.dto.common.toEntity
import me.domain.model.common.TsboardResponseNothing
import me.domain.model.user.TsboardChatHistoryResponse
import me.domain.model.user.TsboardOtherUserInfoResult
import me.domain.model.user.TsboardSendChatResponse
import me.domain.model.user.TsboardUserSafetyStatus
import me.domain.repository.TsboardResponse
import me.domain.repository.TsboardUserChatRepository
import javax.inject.Inject

class TsboardUserChatRepositoryImpl @Inject constructor(
    private val api: TsboardGoapi
) : TsboardUserChatRepository {

    // 다른 사용자의 기본 정보 가져오기
    override suspend fun getOtherUserInfo(
        userUid: Int
    ): TsboardResponse<TsboardOtherUserInfoResult> {
        return try {
            val response = api.getOtherUserInfo(targetUserUid = userUid)
            val result = response.result
            if (!response.success || result == null) {
                TsboardResponse.Error(response.error.ifBlank { "사용자 정보를 찾지 못했습니다" })
            } else {
                TsboardResponse.Success(result.toEntity())
            }
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 상대방과의 대화 기록을 가져오기
    override suspend fun getChatHistory(
        targetUserUid: Int,
        limit: Int,
        token: String
    ): TsboardResponse<TsboardChatHistoryResponse> {
        return try {
            val response = api.getChatHistory(
                authorization = "Bearer $token",
                targetUserUid = targetUserUid,
                limit = limit
            )
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 상대방에게 메시지 보내기
    override suspend fun sendChatMessage(
        targetUserUid: Int,
        message: String,
        token: String
    ): TsboardResponse<TsboardSendChatResponse> {
        return try {
            val response = api.sendChatMessage(
                authorization = "Bearer $token",
                request = SendChatRequestDto(
                    targetUserUid = targetUserUid,
                    message = message
                )
            )
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    override suspend fun getUserSafetyStatus(
        targetUserUid: Int,
        token: String
    ): TsboardResponse<TsboardUserSafetyStatus> {
        return try {
            val response = api.getUserSafetyStatus("Bearer $token", targetUserUid)
            val result = response.result
            if (!response.success || result == null) {
                TsboardResponse.Error(response.error.ifBlank { "안전 설정을 불러오지 못했습니다" })
            } else {
                TsboardResponse.Success(result.toEntity())
            }
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "안전 설정을 불러오지 못했습니다")
        }
    }

    override suspend fun reportUser(
        targetUserUid: Int,
        content: String,
        token: String
    ): TsboardResponse<TsboardResponseNothing> {
        return try {
            TsboardResponse.Success(
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
            TsboardResponse.Error(e.localizedMessage ?: "신고를 접수하지 못했습니다")
        }
    }

    override suspend fun blockUser(
        targetUserUid: Int,
        token: String
    ): TsboardResponse<TsboardResponseNothing> = changeBlockStatus(targetUserUid, token, true)

    override suspend fun unblockUser(
        targetUserUid: Int,
        token: String
    ): TsboardResponse<TsboardResponseNothing> = changeBlockStatus(targetUserUid, token, false)

    private suspend fun changeBlockStatus(
        targetUserUid: Int,
        token: String,
        blocked: Boolean
    ): TsboardResponse<TsboardResponseNothing> {
        return try {
            val request = UserTargetRequestDto(targetUserUid)
            val response = if (blocked) {
                api.blockUser("Bearer $token", request)
            } else {
                api.unblockUser("Bearer $token", request)
            }
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "차단 설정을 변경하지 못했습니다")
        }
    }
}

package me.data.repository

import me.data.remote.api.TsboardGoapi
import me.data.remote.dto.user.toEntity
import me.domain.model.user.TsboardChatHistoryResponse
import me.domain.model.user.TsboardOtherUserInfoResult
import me.domain.model.user.TsboardSendChatResponse
import me.domain.repository.TsboardResponse
import me.domain.repository.TsboardUserRepository
import javax.inject.Inject

class TsboardUserRepositoryImpl @Inject constructor(
    private val api: TsboardGoapi
) : TsboardUserRepository {

    // 다른 사용자의 기본 정보 가져오기
    override suspend fun getOtherUserInfo(
        userUid: Int
    ): TsboardResponse<TsboardOtherUserInfoResult> {
        return try {
            val response = api.getOtherUserInfo(targetUserUid = userUid)
            TsboardResponse.Success(response.result.toEntity())
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
                targetUserUid = targetUserUid,
                message = message
            )
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }
}
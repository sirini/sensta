package me.domain.repository

import me.domain.model.user.TsboardChatHistoryResponse
import me.domain.model.user.TsboardOtherUserInfoResult
import me.domain.model.user.TsboardSendChatResponse
import me.domain.model.user.TsboardUserSafetyStatus
import me.domain.model.common.TsboardResponseNothing

// 다른 사용자의 상호작용 관련 인터페이스
interface TsboardUserChatRepository {
    suspend fun getOtherUserInfo(userUid: Int): TsboardResponse<TsboardOtherUserInfoResult>
    suspend fun getChatHistory(
        targetUserUid: Int,
        limit: Int,
        token: String
    ): TsboardResponse<TsboardChatHistoryResponse>

    suspend fun sendChatMessage(
        targetUserUid: Int,
        message: String,
        token: String
    ): TsboardResponse<TsboardSendChatResponse>

    suspend fun getUserSafetyStatus(
        targetUserUid: Int,
        token: String
    ): TsboardResponse<TsboardUserSafetyStatus>

    suspend fun reportUser(
        targetUserUid: Int,
        content: String,
        token: String
    ): TsboardResponse<TsboardResponseNothing>

    suspend fun blockUser(
        targetUserUid: Int,
        token: String
    ): TsboardResponse<TsboardResponseNothing>

    suspend fun unblockUser(
        targetUserUid: Int,
        token: String
    ): TsboardResponse<TsboardResponseNothing>
}

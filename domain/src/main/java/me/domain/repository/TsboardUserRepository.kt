package me.domain.repository

import me.domain.model.user.TsboardChatHistoryResponse
import me.domain.model.user.TsboardOtherUserInfoResult
import me.domain.model.user.TsboardSendChatResponse

// 다른 사용자의 상호작용 관련 인터페이스
interface TsboardUserRepository {
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
}
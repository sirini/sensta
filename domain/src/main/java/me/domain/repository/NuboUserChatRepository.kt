package me.domain.repository

import me.domain.model.user.NuboChatHistoryResponse
import me.domain.model.user.NuboChatReadResult
import me.domain.model.user.NuboOtherUserInfoResult
import me.domain.model.user.NuboSendChatResponse
import me.domain.model.user.NuboUserSafetyStatus
import me.domain.model.common.NuboResponseNothing

// 다른 사용자의 상호작용 관련 인터페이스
interface NuboUserChatRepository {
    suspend fun getOtherUserInfo(userUid: Int): NuboResponse<NuboOtherUserInfoResult>
    suspend fun getChatHistory(
        targetUserUid: Int,
        limit: Int,
        token: String
    ): NuboResponse<NuboChatHistoryResponse>

    suspend fun markChatRead(
        targetUserUid: Int,
        throughUid: Int,
        token: String
    ): NuboResponse<NuboChatReadResult>

    suspend fun sendChatMessage(
        targetUserUid: Int,
        message: String,
        token: String
    ): NuboResponse<NuboSendChatResponse>

    suspend fun getUserSafetyStatus(
        targetUserUid: Int,
        token: String
    ): NuboResponse<NuboUserSafetyStatus>

    suspend fun reportUser(
        targetUserUid: Int,
        content: String,
        token: String
    ): NuboResponse<NuboResponseNothing>

    suspend fun blockUser(
        targetUserUid: Int,
        token: String
    ): NuboResponse<NuboResponseNothing>

    suspend fun unblockUser(
        targetUserUid: Int,
        token: String
    ): NuboResponse<NuboResponseNothing>
}

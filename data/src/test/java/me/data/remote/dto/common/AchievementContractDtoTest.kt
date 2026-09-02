package me.data.remote.dto.common

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AchievementContractDtoTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `미확인 업적 목록을 앱 모델로 변환한다`() {
        val response = json.decodeFromString<AchievementListResponseDto>(
            """{"success":true,"error":"","code":0,"result":[{"key":"manual-summer","name":"여름 사진전 우수상","description":"좋은 사진을 공유했습니다.","iconKey":"trophy","earnedAt":1000}]}"""
        )

        assertTrue(response.success)
        assertEquals("manual-summer", response.result.single().toEntity().key)
        assertEquals("trophy", response.result.single().iconKey)
    }

    @Test
    fun `확인한 업적 키만 서버에 전송한다`() {
        val encoded = json.encodeToString(AchievementAcknowledgeRequestDto(listOf("manual-summer")))
        assertEquals("""{"keys":["manual-summer"]}""", encoded)
    }
}

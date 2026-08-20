package me.data.remote.dto.user

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.data.remote.dto.home.NotificationListResponseDto
import me.data.remote.dto.home.PushDeviceRequestDto
import me.data.remote.dto.home.toEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SocialContractDtoTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `대화 기록의 서버 정렬 순서와 발신자를 보존한다`() {
        val response = json.decodeFromString<ChatHistoryListResponseDto>(
            """
            {
              "success":true,
              "error":"",
              "code":0,
              "result":[
                {"uid":10,"userUid":1,"message":"먼저 보낸 메시지","timestamp":1000},
                {"uid":11,"userUid":2,"message":"나중 메시지","timestamp":2000}
              ]
            }
            """.trimIndent()
        ).toEntity()

        assertEquals(listOf(10, 11), response.result.map { it.uid })
        assertEquals(listOf(1, 2), response.result.map { it.userUid })
    }

    @Test
    fun `메시지 전송 요청을 JSON 계약으로 직렬화한다`() {
        val body = json.parseToJsonElement(
            json.encodeToString(SendChatRequestDto(targetUserUid = 145, message = "안녕하세요"))
        ).jsonObject

        assertEquals(145, body.getValue("targetUserUid").jsonPrimitive.int)
        assertEquals("안녕하세요", body.getValue("message").jsonPrimitive.content)
    }

    @Test
    fun `메시지 전송 오류에 result가 없어도 역직렬화한다`() {
        val response = json.decodeFromString<SendChatResponseDto>(
            """{"success":false,"error":"전송 권한이 없습니다","code":4}"""
        )

        assertFalse(response.success)
        assertEquals(0, response.result)
    }

    @Test
    fun `알림 목록 응답의 게시글 이동 정보를 읽는다`() {
        val response = json.decodeFromString<NotificationListResponseDto>(
            """
            {
              "success":true,
              "error":"",
              "code":0,
              "result":[{
                "uid":31,
                "fromUser":{"uid":7,"name":"동료 사진가","profile":"/profile.webp"},
                "type":2,
                "id":"photo",
                "boardType":1,
                "postUid":7522,
                "checked":false,
                "timestamp":2000
              }]
            }
            """.trimIndent()
        ).toEntity()

        assertTrue(response.success)
        assertEquals(7522, response.result.single().postUid)
        assertFalse(response.result.single().checked)
    }

    @Test
    fun `사용자 정보 오류에 result가 없어도 역직렬화한다`() {
        val response = json.decodeFromString<OtherUserInfoDto>(
            """{"success":false,"error":"User not found","code":2}"""
        )

        assertFalse(response.success)
        assertNull(response.result)
    }

    @Test
    fun `푸시 기기 요청은 안드로이드 플랫폼을 명시한다`() {
        val body = json.parseToJsonElement(
            json.encodeToString(
                PushDeviceRequestDto(token = "fcm-device-token", platform = "android")
            )
        ).jsonObject

        assertEquals("fcm-device-token", body.getValue("token").jsonPrimitive.content)
        assertEquals("android", body.getValue("platform").jsonPrimitive.content)
    }
}

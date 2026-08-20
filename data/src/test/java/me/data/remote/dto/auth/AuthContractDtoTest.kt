package me.data.remote.dto.auth

import kotlinx.serialization.json.Json
import me.data.remote.dto.common.BooleanResponseDto
import me.data.remote.dto.common.toEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthContractDtoTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `이메일 중복 확인의 불리언 결과를 기존 도메인 응답으로 변환한다`() {
        val response = json.decodeFromString<BooleanResponseDto>(
            """{"success":true,"error":"","code":0,"result":true}"""
        ).toEntity()

        assertTrue(response.success)
        assertEquals("true", response.result)
    }

    @Test
    fun `회원가입 응답에서 이메일 인증 필요 여부를 읽는다`() {
        val response = json.decodeFromString<SignupDto>(
            """
            {
              "success": true,
              "error": "",
              "code": 0,
              "result": {
                "target": 42,
                "requiresVerification": true,
                "completed": false
              }
            }
            """.trimIndent()
        ).toEntity()

        assertEquals(42, response.result.target)
        assertTrue(response.result.requiresVerification)
        assertFalse(response.result.completed)
    }

    @Test
    fun `회원가입 오류 응답에 result가 없어도 역직렬화한다`() {
        val response = json.decodeFromString<SignupDto>(
            """{"success":false,"error":"이미 사용 중입니다","code":5}"""
        ).toEntity()

        assertFalse(response.success)
        assertEquals(0, response.result.target)
    }

    @Test
    fun `모바일 토큰 갱신 응답에서 회전된 토큰 쌍을 읽는다`() {
        val response = json.decodeFromString<UpdateAccessTokenDto>(
            """
            {
              "success": true,
              "error": "",
              "code": 0,
              "result": {"token":"new-access","refresh":"new-refresh"}
            }
            """.trimIndent()
        ).toEntity()

        assertEquals("new-access", response.result?.token)
        assertEquals("new-refresh", response.result?.refresh)
    }

    @Test
    fun `토큰 갱신 오류 응답의 result는 null이다`() {
        val response = json.decodeFromString<UpdateAccessTokenDto>(
            """{"success":false,"error":"유효하지 않은 토큰입니다","code":1}"""
        ).toEntity()

        assertFalse(response.success)
        assertNull(response.result)
    }
}

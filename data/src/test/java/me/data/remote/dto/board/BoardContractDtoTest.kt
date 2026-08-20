package me.data.remote.dto.board

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BoardContractDtoTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `현재 게시글 목록 응답과 추가 설정 필드를 함께 읽는다`() {
        val response = json.decodeFromString<BoardListResponseDto>(
            """
            {
              "success": true,
              "error": "",
              "code": 0,
              "result": {
                "totalPostCount": 1,
                "config": {
                  "uid": 2,
                  "id": "photo",
                  "groupUid": 2,
                  "admin": {"group":1,"board":1},
                  "type": 1,
                  "name": "일상",
                  "info": "우리가 즐기고 사랑한 순간들",
                  "rowCount": 32,
                  "width": 1200,
                  "useCategory": false,
                  "category": [],
                  "level": {"view":0,"write":1,"comment":1,"download":1,"list":0},
                  "point": {"view":0,"write":5,"comment":2,"download":-10},
                  "skinKey": "nubo-basic-board"
                },
                "notices": [],
                "posts": [{
                  "uid": 7522,
                  "title": "선정릉",
                  "content": "<p>도심 속 공간</p>",
                  "submitted": 1787135276537,
                  "modified": 0,
                  "hit": 2,
                  "status": 0,
                  "category": {"uid":1,"name":"일반"},
                  "cover": "/upload/thumbnails/photo.webp",
                  "comment": 0,
                  "like": 0,
                  "liked": false,
                  "writer": {"uid":1,"name":"사진가","profile":"/profile.webp","signature":""}
                }],
                "blackList": [],
                "isAdmin": false
              }
            }
            """.trimIndent()
        ).toEntity()

        assertTrue(response.success)
        assertEquals(7522, response.result.posts.single().uid)
        assertEquals("/upload/thumbnails/photo.webp", response.result.posts.single().cover)
    }

    @Test
    fun `게시글 좋아요 요청은 불리언 JSON으로 직렬화한다`() {
        val body = json.parseToJsonElement(
            json.encodeToString(BoardLikeRequestDto(boardUid = 2, postUid = 7522, liked = true))
        ).jsonObject

        assertEquals(2, body.getValue("boardUid").jsonPrimitive.int)
        assertEquals(7522, body.getValue("postUid").jsonPrimitive.int)
        assertTrue(body.getValue("liked").jsonPrimitive.boolean)
    }

    @Test
    fun `댓글 좋아요 취소 요청도 불리언 JSON으로 직렬화한다`() {
        val body = json.parseToJsonElement(
            json.encodeToString(CommentLikeRequestDto(boardUid = 2, commentUid = 9, liked = false))
        ).jsonObject

        assertFalse(body.getValue("liked").jsonPrimitive.boolean)
    }
}

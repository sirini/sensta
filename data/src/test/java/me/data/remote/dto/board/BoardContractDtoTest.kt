package me.data.remote.dto.board

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.data.remote.dto.photo.ImageDto
import me.data.remote.dto.photo.toEntity
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
                  "writer": {
                    "uid":1,
                    "name":"사진가",
                    "profile":"/profile.webp",
                    "signature":"",
                    "badges":[{
                      "key":"sensta-app",
                      "name":"SENSTA 앱 포토그래퍼",
                      "description":"SENSTA 앱으로 사진을 공유한 사용자입니다.",
                      "iconKey":"aperture",
                      "earnedAt":1787135276537
                    }]
                  }
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
        assertEquals("sensta-app", response.result.posts.single().writer.badges.single().key)
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

    @Test
    fun `댓글 수정 요청은 대상 댓글과 게시글을 함께 직렬화한다`() {
        val body = json.parseToJsonElement(
            json.encodeToString(
                ModifyCommentRequestDto(
                    boardUid = 2,
                    postUid = 7522,
                    modifyTargetUid = 231,
                    content = "수정한 댓글"
                )
            )
        ).jsonObject

        assertEquals(2, body.getValue("boardUid").jsonPrimitive.int)
        assertEquals(7522, body.getValue("postUid").jsonPrimitive.int)
        assertEquals(231, body.getValue("modifyTargetUid").jsonPrimitive.int)
    }

    @Test
    fun `원본 경로를 숨긴 게시글 상세 이미지와 EXIF 응답을 읽는다`() {
        val image = json.decodeFromString<ImageDto>(
            """
            {
              "file": {"uid":7069},
              "thumbnail": {
                "large":"/upload/thumbnails/large.webp",
                "small":"/upload/thumbnails/small.webp"
              },
              "exif": {
                "make":"Apple",
                "model":"iPhone 17",
                "aperture":160,
                "iso":32,
                "focalLength":52,
                "exposure":4901,
                "width":4032,
                "height":3024,
                "date":1783193189000
              },
              "description":"도심 풍경"
            }
            """.trimIndent()
        ).toEntity()

        assertEquals(7069, image.file.uid)
        assertEquals("iPhone 17", image.exif.model)
        assertEquals(4032, image.exif.width)
    }

    @Test
    fun `현재 댓글 목록 응답을 읽는다`() {
        val response = json.decodeFromString<CommentListResponseDto>(
            """
            {
              "success":true,
              "error":"",
              "code":0,
              "result":{
                "boardUid":2,
                "sinceUid":0,
                "totalCommentCount":1,
                "comments":[{
                  "uid":231,
                  "replyUid":231,
                  "postUid":7520,
                  "writer":{"uid":1,"name":"사진가","profile":"/profile.webp","signature":""},
                  "like":0,
                  "liked":false,
                  "submitted":1778822019491,
                  "modified":0,
                  "status":0,
                  "content":"<p>멋진 사진들이네요!</p>"
                }]
              }
            }
            """.trimIndent()
        ).toEntity()

        assertEquals(2, response.result.boardUid)
        assertEquals(231, response.result.comments.single().uid)
    }

    @Test
    fun `내 작품 스튜디오의 누적 성과와 페이지를 읽는다`() {
        val response = json.decodeFromString<StudioResponseDto>(
            """
            {
              "success":true,
              "error":"",
              "code":0,
              "result":{
                "summary":{
                  "postCount":12,
                  "photoCount":26,
                  "viewCount":1520,
                  "likeCount":341,
                  "commentCount":78
                },
                "posts":{
                  "page":1,
                  "limit":20,
                  "totalCount":12,
                  "hasNext":false,
                  "items":[{
                    "uid":7522,
                    "title":"여름 오후",
                    "cover":"/upload/thumbnails/summer.webp",
                    "submitted":1788012345000,
                    "modified":1788012345000,
                    "status":0,
                    "imageCount":3,
                    "hit":210,
                    "like":42,
                    "comment":8
                  }]
                }
              }
            }
            """.trimIndent()
        ).toEntity()

        assertEquals(12L, response.summary.postCount)
        assertEquals(26L, response.summary.photoCount)
        assertEquals(1520L, response.summary.viewCount)
        assertEquals(341L, response.summary.likeCount)
        assertEquals(78L, response.summary.commentCount)
        assertFalse(response.posts.hasNext)
        assertEquals(3L, response.posts.items.single().imageCount)
        assertEquals("/upload/thumbnails/summer.webp", response.posts.items.single().cover)
    }

    @Test
    fun `내 작품 스튜디오 오류 응답은 result 없이도 읽는다`() {
        val response = json.decodeFromString<StudioResponseDto>(
            """{"success":false,"error":"invalid sort","code":3}"""
        )

        assertFalse(response.success)
        assertEquals(3, response.code)
        assertEquals(null, response.result)
    }

    @Test
    fun `글쓰기 오류 응답에 result가 없어도 역직렬화한다`() {
        val response = json.decodeFromString<WriteResponseDto>(
            """{"success":false,"error":"invalid title","code":1}"""
        )

        assertFalse(response.success)
        assertEquals(0, response.result)
    }
}

package me.sensta.ui.screen.user

import org.junit.Assert.assertEquals
import org.junit.Test

class ChatMessageTextTest {
    @Test
    fun `한글 영문 숫자 밑줄 해시태그를 찾아 원문 순서를 보존한다`() {
        val segments = splitChatMessage("오늘 #여름사진 그리고 #film_2026 같이 봐요")

        assertEquals(
            listOf(
                ChatMessageSegment.Plain("오늘 "),
                ChatMessageSegment.Hashtag("여름사진"),
                ChatMessageSegment.Plain(" 그리고 "),
                ChatMessageSegment.Hashtag("film_2026"),
                ChatMessageSegment.Plain(" 같이 봐요")
            ),
            segments
        )
    }

    @Test
    fun `단어에 붙은 샵 문자는 해시태그로 만들지 않는다`() {
        assertEquals(
            listOf(ChatMessageSegment.Plain("C# 문법과 사진#설명")),
            splitChatMessage("C# 문법과 사진#설명")
        )
    }
}

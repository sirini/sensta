package me.sensta.sync

import me.domain.model.board.NuboCategory
import me.domain.model.board.NuboComment
import me.domain.model.board.NuboPost
import me.domain.model.common.NuboWriter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class BoardStateSyncTest {
    private val writer = NuboWriter(7, "작가", "", "")

    @Test
    fun `좋아요 변경은 횟수를 한 번만 보정한다`() {
        val post = post(liked = false, like = 3)
        val mutation = BoardMutation.PostLikeChanged(postUid = post.uid, liked = true)

        val changed = requireNotNull(post.applyMutation(mutation))
        val appliedAgain = requireNotNull(changed.applyMutation(mutation))

        assertTrue(changed.liked)
        assertEquals(4, changed.like)
        assertEquals(changed, appliedAgain)
    }

    @Test
    fun `서버의 이전 좋아요 값에도 현재 사용자 동작을 덮어쓴다`() {
        val sync = BoardStateSync()
        sync.changePostLike(postUid = 10, liked = true)

        val stale = requireNotNull(sync.applyToPost(post(liked = false, like = 3)))
        val alreadyUpdated = requireNotNull(sync.applyToPost(post(liked = true, like = 4)))

        assertTrue(stale.liked)
        assertEquals(4, stale.like)
        assertEquals(4, alreadyUpdated.like)
    }

    @Test
    fun `stale post response does not double count a rapid like reversal`() {
        val sync = BoardStateSync()
        sync.applyToPost(post(liked = true, like = 1))

        sync.changePostLike(postUid = 10, liked = false, expectedLikeCount = 0)
        assertEquals(0, requireNotNull(sync.applyToPost(post(liked = true, like = 1))).like)

        sync.changePostLike(postUid = 10, liked = true, expectedLikeCount = 1)
        val staleResponse = requireNotNull(sync.applyToPost(post(liked = false, like = 1)))

        assertTrue(staleResponse.liked)
        assertEquals(1, staleResponse.like)
    }

    @Test
    fun `stale comment response does not double count a rapid like reversal`() {
        val sync = BoardStateSync()
        sync.applyToComments(10, listOf(comment(liked = true, like = 1)))

        sync.changeCommentLike(commentUid = 31, liked = false, expectedLikeCount = 0)
        assertEquals(
            0,
            sync.applyToComments(10, listOf(comment(liked = true, like = 1))).single().like
        )

        sync.changeCommentLike(commentUid = 31, liked = true, expectedLikeCount = 1)
        val staleResponse = sync.applyToComments(
            10,
            listOf(comment(liked = false, like = 1))
        ).single()

        assertTrue(staleResponse.liked)
        assertEquals(1, staleResponse.like)
    }

    @Test
    fun `댓글 작성 보정은 서버 재조회 성공 뒤 중복 반영되지 않는다`() {
        val sync = BoardStateSync()
        val comment = comment(uid = 55)
        sync.addComment(comment)

        assertEquals(2, requireNotNull(sync.applyToPost(post(comment = 1))).comment)
        assertEquals(listOf(55), sync.applyToComments(10, emptyList()).map { it.uid })

        sync.reconcileComments(postUid = 10, serverComments = listOf(comment))
        assertEquals(2, requireNotNull(sync.applyToPost(post(comment = 2))).comment)
    }

    @Test
    fun `댓글 작성 전에 시작한 상세 조회는 늦게 끝나도 이전 댓글 수를 덮어쓰지 않는다`() {
        val sync = BoardStateSync()
        sync.applyToPost(post(comment = 1))
        val requestRevision = sync.revision

        sync.addComment(comment(uid = 56))
        sync.reconcileComments(postUid = 10, serverComments = listOf(comment(uid = 56)))

        val lateStaleResponse = requireNotNull(
            sync.applyToPost(post(comment = 1), requestRevision = requestRevision)
        )
        assertEquals(2, lateStaleResponse.comment)
    }

    @Test
    fun `댓글 재조회가 이전 목록이면 낙관적 댓글을 유지한다`() {
        val sync = BoardStateSync()
        val added = comment(uid = 57)
        sync.addComment(added)

        sync.reconcileComments(postUid = 10, serverComments = emptyList())

        assertEquals(listOf(57), sync.applyToComments(10, emptyList()).map { it.uid })
    }

    @Test
    fun `확정 후 갱신된 댓글 수도 더 오래된 응답으로 되돌아가지 않는다`() {
        val sync = BoardStateSync()
        sync.applyToPost(post(comment = 1))
        val staleRevision = sync.revision
        sync.addComment(comment(uid = 58))
        val currentRevision = sync.revision

        sync.applyToPost(post(comment = 2), requestRevision = currentRevision)
        sync.applyToPost(post(comment = 3), requestRevision = currentRevision)

        assertEquals(
            3,
            requireNotNull(sync.applyToPost(post(comment = 1), staleRevision)).comment
        )
    }

    @Test
    fun `삭제와 수정 이벤트가 모든 목록 모델에 적용된다`() {
        val edited = post().applyMutation(
            BoardMutation.PostEdited(10, "새 제목", "새 내용", listOf("태그"))
        )
        val removed = post().applyMutation(BoardMutation.PostRemoved(10))

        assertEquals("새 제목", edited?.title)
        assertEquals("새 내용", edited?.content)
        assertNull(removed)
    }

    @Test
    fun `댓글 좋아요와 삭제도 원본 모델을 직접 바꾼다`() {
        val liked = listOf(comment()).applyCommentMutation(
            BoardMutation.CommentLikeChanged(commentUid = 31, liked = true)
        )
        val removed = liked.applyCommentMutation(
            BoardMutation.CommentRemoved(commentUid = 31, postUid = 10, keepPlaceholder = false)
        )

        assertTrue(liked.single().liked)
        assertEquals(1, liked.single().like)
        assertTrue(removed.isEmpty())
    }

    private fun post(liked: Boolean = false, like: Int = 0, comment: Int = 0) = NuboPost(
        uid = 10,
        title = "제목",
        content = "내용",
        submitted = LocalDateTime.of(2026, 9, 1, 12, 0),
        hit = 0,
        status = 0,
        category = NuboCategory(5, "사진"),
        cover = "",
        comment = comment,
        like = like,
        liked = liked,
        writer = writer
    )

    private fun comment(uid: Int = 31, liked: Boolean = false, like: Int = 0) = NuboComment(
        uid = uid,
        replyUid = uid,
        postUid = 10,
        writer = writer,
        like = like,
        liked = liked,
        submitted = LocalDateTime.of(2026, 9, 1, 12, 0),
        status = 0,
        content = "댓글 내용"
    )
}

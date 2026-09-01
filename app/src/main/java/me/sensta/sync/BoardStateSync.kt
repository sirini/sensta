package me.sensta.sync

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.channels.BufferOverflow
import me.domain.model.board.NuboBoardViewResponse
import me.domain.model.board.NuboComment
import me.domain.model.board.NuboPost
import me.domain.model.common.NuboTag
import javax.inject.Inject
import javax.inject.Singleton

sealed interface BoardMutation {
    data class PostLikeChanged(val postUid: Int, val liked: Boolean) : BoardMutation
    data class PostCommentCountChanged(val postUid: Int, val delta: Int) : BoardMutation
    data class PostEdited(
        val postUid: Int,
        val title: String,
        val content: String,
        val tags: List<String>
    ) : BoardMutation

    data class PostRemoved(val postUid: Int) : BoardMutation
    data class CommentAdded(val comment: NuboComment) : BoardMutation
    data class CommentLikeChanged(val commentUid: Int, val liked: Boolean) : BoardMutation
    data class CommentEdited(val commentUid: Int, val content: String) : BoardMutation
    data class CommentRemoved(
        val commentUid: Int,
        val postUid: Int,
        val keepPlaceholder: Boolean
    ) : BoardMutation
}

/**
 * 화면별 ViewModel이 따로 보관하는 게시글/댓글 상태를 한 번의 사용자 동작으로 맞춘다.
 * 서버 반영 직후 시작된 조회가 이전 값을 반환하더라도 세션 안의 최신 사용자 동작을 우선한다.
 */
@Singleton
class BoardStateSync @Inject constructor() {
    private val _mutations = MutableSharedFlow<BoardMutation>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val mutations = _mutations.asSharedFlow()

    private data class LikeState(
        val liked: Boolean,
        val expectedCount: Int?,
        val revision: Long
    )

    private val likedPosts = mutableMapOf<Int, LikeState>()
    private val knownPostLikeCounts = mutableMapOf<Int, Int>()
    private val editedPosts = mutableMapOf<Int, BoardMutation.PostEdited>()
    private val removedPosts = mutableSetOf<Int>()
    private data class PendingCommentCount(
        val delta: Int,
        val expected: Int?,
        val revision: Long,
        val settled: Boolean = false
    )

    private var stateRevision = 0L
    private val knownCommentCounts = mutableMapOf<Int, Int>()
    private val pendingCommentCounts = mutableMapOf<Int, PendingCommentCount>()
    private val addedComments = mutableMapOf<Int, MutableMap<Int, NuboComment>>()
    private val likedComments = mutableMapOf<Int, LikeState>()
    private val knownCommentLikeCounts = mutableMapOf<Int, Int>()
    private val editedComments = mutableMapOf<Int, String>()
    private data class RemovedCommentState(val postUid: Int, val keepPlaceholder: Boolean)

    private val removedComments = mutableMapOf<Int, RemovedCommentState>()

    @get:Synchronized
    val revision: Long get() = stateRevision

    @Synchronized
    fun changePostLike(postUid: Int, liked: Boolean, expectedLikeCount: Int? = null) {
        val revision = advanceRevision()
        likedPosts[postUid] = LikeState(
            liked = liked,
            expectedCount = expectedLikeCount?.coerceAtLeast(0)
                ?: knownPostLikeCounts[postUid]?.let { count ->
                    (count + if (liked) 1 else -1).coerceAtLeast(0)
                },
            revision = revision
        )
        emit(BoardMutation.PostLikeChanged(postUid, liked))
    }

    @Synchronized
    fun editPost(postUid: Int, title: String, content: String, tags: List<String>) {
        advanceRevision()
        val mutation = BoardMutation.PostEdited(postUid, title, content, tags)
        editedPosts[postUid] = mutation
        emit(mutation)
    }

    @Synchronized
    fun removePost(postUid: Int) {
        advanceRevision()
        removedPosts += postUid
        emit(BoardMutation.PostRemoved(postUid))
    }

    @Synchronized
    fun addComment(comment: NuboComment) {
        val revision = advanceRevision()
        addedComments.getOrPut(comment.postUid, ::mutableMapOf)[comment.uid] = comment
        changePendingCommentCount(comment.postUid, delta = 1, revision = revision)
        emit(BoardMutation.CommentAdded(comment))
        emit(BoardMutation.PostCommentCountChanged(comment.postUid, 1))
    }

    @Synchronized
    fun changeCommentLike(commentUid: Int, liked: Boolean, expectedLikeCount: Int? = null) {
        val revision = advanceRevision()
        likedComments[commentUid] = LikeState(
            liked = liked,
            expectedCount = expectedLikeCount?.coerceAtLeast(0)
                ?: knownCommentLikeCounts[commentUid]?.let { count ->
                    (count + if (liked) 1 else -1).coerceAtLeast(0)
                },
            revision = revision
        )
        emit(BoardMutation.CommentLikeChanged(commentUid, liked))
    }

    @Synchronized
    fun editComment(commentUid: Int, content: String) {
        advanceRevision()
        editedComments[commentUid] = content
        emit(BoardMutation.CommentEdited(commentUid, content))
    }

    @Synchronized
    fun removeComment(commentUid: Int, postUid: Int, keepPlaceholder: Boolean) {
        val revision = advanceRevision()
        removedComments[commentUid] = RemovedCommentState(postUid, keepPlaceholder)
        val delta = if (keepPlaceholder) 0 else -1
        changePendingCommentCount(postUid, delta, revision)
        emit(BoardMutation.CommentRemoved(commentUid, postUid, keepPlaceholder))
        if (delta != 0) emit(BoardMutation.PostCommentCountChanged(postUid, delta))
    }

    /** 서버 목록에서 실제 반영을 확인한 댓글만 임시 보정 대상에서 제외한다. */
    @Synchronized
    fun reconcileComments(postUid: Int, serverComments: List<NuboComment>) {
        val commentsByUid = serverComments.associateBy(NuboComment::uid)

        addedComments[postUid]?.let { pendingComments ->
            val confirmedUids = pendingComments.keys.filter(commentsByUid::containsKey)
            confirmedUids.forEach { commentUid ->
                pendingComments.remove(commentUid)
                likedComments.remove(commentUid)
                editedComments.remove(commentUid)
            }
            if (pendingComments.isEmpty()) addedComments.remove(postUid)
        }

        removedComments.entries.removeAll { (commentUid, state) ->
            if (state.postUid != postUid) return@removeAll false
            val serverComment = commentsByUid[commentUid]
            serverComment == null ||
                serverComment.status != 0 ||
                serverComment.content == "(deleted)"
        }
    }

    @Synchronized
    fun applyToPosts(
        posts: List<NuboPost>,
        requestRevision: Long = stateRevision
    ): List<NuboPost> = posts.mapNotNull { applyToPost(it, requestRevision) }

    @Synchronized
    fun applyToPost(post: NuboPost, requestRevision: Long = stateRevision): NuboPost? {
        if (post.uid in removedPosts) return null

        var result = post
        likedPosts[post.uid]?.let { likeState ->
            if (requestRevision < likeState.revision || result.liked != likeState.liked) {
                result = result.copy(
                    liked = likeState.liked,
                    like = likeState.expectedCount
                        ?: (result.like + if (likeState.liked) 1 else -1).coerceAtLeast(0)
                )
            }
        }
        knownPostLikeCounts[post.uid] = result.like
        pendingCommentCounts[post.uid]?.let { pending ->
            if (pending.settled && requestRevision >= pending.revision) {
                pendingCommentCounts[post.uid] = pending.copy(expected = result.comment)
                knownCommentCounts[post.uid] = result.comment
            } else {
                val commentCount = pending.expected
                    ?: (result.comment + pending.delta).coerceAtLeast(0)
                val serverHasCaughtUp = pending.expected != null &&
                    requestRevision >= pending.revision &&
                    when {
                        pending.delta > 0 -> result.comment >= commentCount
                        pending.delta < 0 -> result.comment <= commentCount
                        else -> result.comment == commentCount
                    }

                if (serverHasCaughtUp) {
                    pendingCommentCounts[post.uid] = pending.copy(
                        expected = result.comment,
                        settled = true
                    )
                    knownCommentCounts[post.uid] = result.comment
                } else {
                    result = result.copy(comment = commentCount)
                    pendingCommentCounts[post.uid] = pending.copy(expected = commentCount)
                    knownCommentCounts[post.uid] = commentCount
                }
            }
        } ?: run {
            knownCommentCounts[post.uid] = result.comment
        }
        editedPosts[post.uid]?.let { edit ->
            result = result.copy(title = edit.title, content = edit.content)
        }
        return result
    }

    @Synchronized
    fun applyToPostView(
        view: NuboBoardViewResponse,
        requestRevision: Long = stateRevision
    ): NuboBoardViewResponse {
        val post = applyToPost(view.result.post, requestRevision) ?: return view
        val edit = editedPosts[post.uid]
        val tags = edit?.tags?.mapIndexed { index, name -> NuboTag(uid = -index - 1, name = name) }
            ?: view.result.tags
        return view.copy(result = view.result.copy(post = post, tags = tags))
    }

    @Synchronized
    fun applyToComments(
        postUid: Int,
        comments: List<NuboComment>,
        requestRevision: Long = stateRevision
    ): List<NuboComment> {
        val serverAndLocal = (comments + addedComments[postUid].orEmpty().values)
            .distinctBy(NuboComment::uid)
        return serverAndLocal.mapNotNull { comment ->
            when (removedComments[comment.uid]?.keepPlaceholder) {
                false -> null
                true -> comment.copy(content = "(deleted)")
                null -> {
                    var result = comment
                    likedComments[comment.uid]?.let { likeState ->
                        if (requestRevision < likeState.revision || result.liked != likeState.liked) {
                            result = result.copy(
                                liked = likeState.liked,
                                like = likeState.expectedCount
                                    ?: (result.like + if (likeState.liked) 1 else -1).coerceAtLeast(0)
                            )
                        }
                    }
                    knownCommentLikeCounts[comment.uid] = result.like
                    editedComments[comment.uid]?.let { content ->
                        result = result.copy(content = content)
                    }
                    result
                }
            }
        }
    }

    private fun emit(mutation: BoardMutation) {
        _mutations.tryEmit(mutation)
    }

    private fun advanceRevision(): Long {
        stateRevision += 1
        return stateRevision
    }

    private fun changePendingCommentCount(postUid: Int, delta: Int, revision: Long) {
        val previous = pendingCommentCounts[postUid]
        val expected = if (previous?.settled == false) {
            previous.expected?.plus(delta)
        } else {
            knownCommentCounts[postUid]?.plus(delta)
        }
        pendingCommentCounts[postUid] = PendingCommentCount(
            delta = if (previous?.settled == false) previous.delta + delta else delta,
            expected = expected?.coerceAtLeast(0),
            revision = revision
        )
        expected?.let { knownCommentCounts[postUid] = it.coerceAtLeast(0) }
    }
}

fun NuboPost.applyMutation(mutation: BoardMutation): NuboPost? = when (mutation) {
    is BoardMutation.PostLikeChanged -> if (uid != mutation.postUid || liked == mutation.liked) this else copy(
        liked = mutation.liked,
        like = (like + if (mutation.liked) 1 else -1).coerceAtLeast(0)
    )
    is BoardMutation.PostCommentCountChanged -> if (uid != mutation.postUid) this else copy(
        comment = (comment + mutation.delta).coerceAtLeast(0)
    )
    is BoardMutation.PostEdited -> if (uid != mutation.postUid) this else copy(
        title = mutation.title,
        content = mutation.content
    )
    is BoardMutation.PostRemoved -> takeUnless { uid == mutation.postUid }
    else -> this
}

fun List<NuboPost>.applyPostMutation(mutation: BoardMutation): List<NuboPost> =
    mapNotNull { it.applyMutation(mutation) }

fun NuboBoardViewResponse.applyMutation(mutation: BoardMutation): NuboBoardViewResponse {
    val changedPost = result.post.applyMutation(mutation) ?: return this
    val changedTags = if (mutation is BoardMutation.PostEdited && mutation.postUid == changedPost.uid) {
        mutation.tags.mapIndexed { index, name -> NuboTag(uid = -index - 1, name = name) }
    } else {
        result.tags
    }
    return copy(result = result.copy(post = changedPost, tags = changedTags))
}

fun List<NuboComment>.applyCommentMutation(mutation: BoardMutation): List<NuboComment> = when (mutation) {
    is BoardMutation.CommentAdded -> if (any { it.uid == mutation.comment.uid }) this else this + mutation.comment
    is BoardMutation.CommentLikeChanged -> map { comment ->
        if (comment.uid != mutation.commentUid || comment.liked == mutation.liked) comment else comment.copy(
            liked = mutation.liked,
            like = (comment.like + if (mutation.liked) 1 else -1).coerceAtLeast(0)
        )
    }
    is BoardMutation.CommentEdited -> map { comment ->
        if (comment.uid == mutation.commentUid) comment.copy(content = mutation.content) else comment
    }
    is BoardMutation.CommentRemoved -> if (mutation.keepPlaceholder) {
        map { comment ->
            if (comment.uid == mutation.commentUid) comment.copy(content = "(deleted)") else comment
        }
    } else {
        filterNot { it.uid == mutation.commentUid }
    }
    else -> this
}

package me.sensta.viewmodel.common

import androidx.compose.runtime.asIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor() : ViewModel() {
    private val _postUid = mutableIntStateOf(0)
    val postUid = _postUid.asIntState()

    private val _pagerIndex = mutableIntStateOf(0)
    val pagerIndex = _pagerIndex.asIntState()
    
    private var _showCommentDialog by mutableStateOf(false)
    val showCommentDialog: Boolean get() = _showCommentDialog

    // 이미 목록에서 가져왔던 사진 정보들 저장하기
    fun updatePostUid(postUid: Int) {
        _postUid.intValue = postUid
    }

    // 여러 장의 사진을 좌우로 넘길 때마다 페이저 인덱스도 업데이트
    fun updatePagerIndex(index: Int) {
        _pagerIndex.intValue = index
    }

    // 댓글 달기용 다이얼로그 띄우기
    fun openWriteCommentDialog(postUid: Int? = null) {
        postUid?.let { _postUid.intValue = postUid }
        _showCommentDialog = true
    }

    // 댓글 달기용 다이얼로그 닫기
    fun closeWriteCommentDialog() {
        _showCommentDialog = false
    }
}